package se.lth.cs.student.battle3d.io 

import se.lth.cs.student.battle3d.main.configurations as cfgs
import se.lth.cs.student.battle3d.util.Singleton
import se.lth.cs.student.battle3d.event.ThreadQueue as TQ
import se.lth.cs.student.battle3d.gfx.Renderer

import org.lwjgl.opengl.GL

import org.lwjgl.system.MemoryUtil

import org.lwjgl.glfw.{
  GLFW,
  GLFWCharModsCallback,
  GLFWCursorEnterCallback,
  GLFWCursorPosCallback,
  GLFWDropCallback,
  GLFWErrorCallback,
  GLFWFramebufferSizeCallback,
  GLFWKeyCallback,
  GLFWMonitorCallback,
  GLFWMouseButtonCallback,
  GLFWScrollCallback,
  GLFWWindowCloseCallback,
  GLFWWindowFocusCallback,
  GLFWWindowIconifyCallback,
  GLFWWindowMaximizeCallback,
  GLFWWindowPosCallback,
  GLFWWindowRefreshCallback,
  GLFWWindowSizeCallback
}
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import jglm.{
  Vec3
}

private final case class InputState(
  //Inclusive: anyone may be true simultaneously
  val hasShift: Boolean,
  val hasCtrl:  Boolean,
  val hasAlt:   Boolean,
  val hasSuper: Boolean,
  //Exclusive: Only one may be true at a time
  val isRelease:Boolean,
  val isPressed:Boolean,
  val isRepeat :Boolean)
private object InputState:
  private def hasModifier(mods: Int, mod: Int) : Boolean = (mods & mod) != 0
  
  def apply(action: Int, mods: Int): InputState = 
    new InputState(
      hasShift  = hasModifier(mods, GLFW.GLFW_MOD_SHIFT),
      hasCtrl   = hasModifier(mods, GLFW.GLFW_MOD_CONTROL),
      hasAlt    = hasModifier(mods, GLFW.GLFW_MOD_ALT),
      hasSuper  = hasModifier(mods, GLFW.GLFW_MOD_SUPER),
      isRelease = action == GLFW.GLFW_RELEASE,
      isPressed = action == GLFW.GLFW_PRESS,
      isRepeat  = action == GLFW.GLFW_REPEAT,
    )
end InputState

private object Callbacks:

    final class Error extends GLFWErrorCallback:
        override def invoke(error: Int, pDesc: Long): Unit = 
            val description: String = MemoryUtil.memByteBuffer(pDesc, 1024)
                .array()
                .map{_.asInstanceOf[Char]}
                .toString()
            Logger.printFatal(s"GLFW Error $error: description")
    
    
    final class KeysInput       extends GLFWKeyCallback:
      var keyDownTimeMS: Long = 0
      val accelerationMaxTime: Long = 5000
      val acceleration = 0.1f // units/s^2

      var x = 0
      var y = 0
      var z = 0
      override def invoke(window: Long, keyCode: Int, scanCode: Int, action: Int, mods: Int): Unit =
        val inputState = InputState(action, mods)
        keyCode match

          case 'W'|'w' => //Forward
            if inputState.isPressed then 
              x += 1
            if inputState.isRelease then 
              x -= 1
            
          case 'A'|'a' => //Left
            if inputState.isPressed then 
              z -= 1
            if inputState.isRelease then 
              z += 1

          case 'S'|'s' => //Backward
            if inputState.isPressed then 
              x -= 1
            if inputState.isRelease then 
              x += 1

          case 'D'|'d' => //Right
            if inputState.isPressed then 
              z += 1
            if inputState.isRelease then 
              z -= 1

          case 'Q'|'q' => //Up
            if inputState.isPressed then 
              y += 1
            if inputState.isRelease then 
              y -= 1

          case 'E'|'e' => //Down
            if inputState.isPressed then 
              y -= 1
            if inputState.isRelease then 
              y += 1
          
          if x == 0 && y == 0 && z == 0 then 
            keyDownTimeMS = 0
          else if keyDownTimeMS == 0 then 
            keyDownTimeMS = System.currentTimeMillis()
          //ms
          val dt = scala.math.max(System.currentTimeMillis() - keyDownTimeMS, accelerationMaxTime) //don't accelerate any more after 5 seconds
          val speed = acceleration*dt*1000.toFloat + 1.0f
          
          //FIXME: discrepency in system
          TQ.getQueue("Renderer").enqueue(Renderer.MoveCamera(Vec3(x,y,z).normalize().times(speed)))
          TQ.releaseQueue("Renderer")
        
    final class MouseButton     extends GLFWMouseButtonCallback:
      override def invoke(window: Long, button: Int, action: Int, mods: Int): Unit = 
        val inputState = InputState(action, mods)
        if button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !inputState.hasShift then 
          Display.freeMode = false
        else
          Display.freeMode = true

        
    final class CursorPos       extends GLFWCursorPosCallback:
      override def invoke(window: Long, xpos: Double, ypos: Double): Unit = 
        val q = TQ.getQueue("Renderer")
        val angleX = (xpos.toFloat - (Display.dim(0)/2))/Display.dim(0)
        val angleY = (ypos.toFloat - (Display.dim(1)/2))/Display.dim(1)
        
        if 
          GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) != GLFW.GLFW_FALSE &&
          !Display.freeMode
        then 
          GLFW.glfwSetCursorPos(window, Display.dim(0)/2, Display.dim(1)/2)
          q.enqueue(Renderer.RotateCamera(angleX,'X'))
          q.enqueue(Renderer.RotateCamera(angleY,'Y'))
          TQ.releaseQueue("Renderer")
    end CursorPos
        
    final class CursorEnter     extends GLFWCursorEnterCallback:
      
      override def invoke(window: Long, entered: Boolean): Unit = 
        val left = !entered
    
    final class FramebufferSize extends GLFWFramebufferSizeCallback:

      override def invoke(window: Long, width: Int, height: Int): Unit = 
        TQ.getQueue("Renderer").enqueue(new Renderer.ResizeWindow(width, height))

    final class GetFocus extends GLFWWindowFocusCallback:
      override def invoke(window: Long, focused: Boolean): Unit = 
        if focused then 
          GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN)
          GLFW.glfwSetCursorPos(window, Display.dim(0)/2, Display.dim(1)/2)
        else
          GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)


        

/** Main Window, onto whose drawing are, the Renderer performs it's draws.
  * 
  * In the background this will perform IO operations using GLFW will callbacks from the event object named
  * MainWindowEvent.scala
  */
object Display extends Singleton:
    

    GLFW.glfwInit()
    /** Handle to the window object*/
    private var hWindow = 0L 
    GLFW.glfwSetErrorCallback(new Callbacks.Error)

    var freeMode: Boolean = true


    def init(): Unit = 
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, 1)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 1)
        //Use OpenGL 4.5 Core
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, 
          if cfgs("debug").toLowerCase() == "true" then 
            GLFW.GLFW_OPENGL_DEBUG_CONTEXT 
          else 
            GLFW.GLFW_OPENGL_CORE_PROFILE)

        hWindow = GLFW.glfwCreateWindow(cfgs("width").toInt, cfgs("height").toInt, cfgs("title"), 0, 0)
        if (hWindow == 0L) then 
            Logger.printFatal("Could not create window")

        GLFW.glfwSetKeyCallback         (hWindow, new Callbacks.KeysInput)
        GLFW.glfwSetCursorEnterCallback (hWindow, new Callbacks.CursorEnter)
        GLFW.glfwSetCursorPosCallback   (hWindow, new Callbacks.CursorPos)
        GLFW.glfwSetWindowFocusCallback (hWindow, new Callbacks.GetFocus)


        
    def associateThisThreadWithGL(): Unit = 
        GLFW.glfwMakeContextCurrent(hWindow)
        GL.createCapabilities()
      
    def swapBuffers(): Unit = 
      GLFW.glfwSwapBuffers(hWindow)

    def isRunning: Boolean = GLFW.glfwWindowShouldClose(hWindow)

    def dim: (Int, Int) = 
      val width = Array.fill[Int](1)(0)
      val height =Array.fill[Int](1)(0)
      GLFW.glfwGetWindowSize(hWindow, width, height)
      (width(0), height(0))
        
    def destroy(): Unit = 
      glfwFreeCallbacks(hWindow)
      GLFW.glfwDestroyWindow(hWindow)
      GLFW.glfwTerminate()