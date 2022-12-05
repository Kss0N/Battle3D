package se.lth.cs.student.battle3d.io 

import se.lth.cs.student.battle3d.main.configurations as cfgs
import se.lth.cs.student.battle3d.util.Singleton

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


private object Callbacks:

    final class Error extends GLFWErrorCallback:

        override def invoke(error: Int, pDesc: Long): Unit = 
            val description: String = MemoryUtil.memByteBuffer(pDesc, 1024)
                .array()
                .map{_.asInstanceOf[Char]}
                .toString()
            Logger.printFatal(s"GLFW Error $error: description")
    
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
    
    final class KeysInput       extends GLFWKeyCallback:
      override def invoke(window: Long, keyCode: Int, scanCode: Int, action: Int, mods: Int): Unit =
        val key = keyCode.toChar
        val inputState = InputState(action, mods)
        //TODO: Forward to event handling
    final class MouseButton     extends GLFWMouseButtonCallback:
      override def invoke(window: Long, button: Int, action: Int, mods: Int): Unit = 
        val inputState = InputState(action, mods)
        //TODO: Forward to event handling
    final class CursorPos       extends GLFWCursorPosCallback:
      
      override def invoke(window: Long, xpos: Double, ypos: Double): Unit = 
        ???
    final class CursorEnter     extends GLFWCursorEnterCallback:
      
      override def invoke(window: Long, entered: Boolean): Unit = 
        val left = !entered
    final class FramebufferSize extends GLFWFramebufferSizeCallback:

      override def invoke(window: Long, width: Int, height: Int): Unit = 
        ???


        

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


        
    def associateThisThreadWithGL(): Unit = 
        GLFW.glfwMakeContextCurrent(hWindow)
        GL.createCapabilities()
      
    def swapBuffers(): Unit = 
      GLFW.glfwSwapBuffers(hWindow)

    def isRunning: Boolean = GLFW.glfwWindowShouldClose(hWindow)
        
    def destroy(): Unit = 
      glfwFreeCallbacks(hWindow)
      GLFW.glfwDestroyWindow(hWindow)
      GLFW.glfwTerminate()