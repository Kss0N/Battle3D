package se.cs.lth.student.battle3d.render

import com.jogamp.opengl.DebugGL4
import com.jogamp.opengl.FBObject
import com.jogamp.opengl.DefaultGLCapabilitiesChooser
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLBase
import com.jogamp.opengl.GLContext
import com.jogamp.opengl.GLCapabilities

import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.GLPipelineFactory
import com.jogamp.opengl.GLProfile

import com.jogamp.opengl.util.Animator

import com.jogamp.opengl.util.GLBuffers

import com.jogamp.newt.opengl.GLWindow

import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.KeyListener
import com.jogamp.newt.event.MouseListener
import com.jogamp.newt.event.WindowListener

import com.jogamp.opengl.GL2GL3
import com.jogamp.opengl.GL2ES3
import com.jogamp.opengl.GL2ES2

import se.lth.cs.student.battle3d.io.Logger
import se.lth.cs.student.battle3d.main.Battle3D

import com.jogamp.newt.event.MouseEvent
import com.jogamp.newt.event.WindowEvent
import com.jogamp.newt.event.WindowUpdateEvent
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.newt.event.GestureHandler
import com.jogamp.newt.event.InputEvent

import com.jogamp.nativewindow.SurfaceUpdatedListener
import com.jogamp.nativewindow.NativeSurface

object Renderer:
    val profile = GLProfile.get(GLProfile.GL4)
    val capabilities = GLCapabilities(profile)
    val window = GLWindow.create(capabilities)
    val myGL = window.getGL().getGL4bc()
    val gl = DebugGL4(myGL)

    


    
    class MyKeyListener extends KeyListener:

        override def keyPressed(e : KeyEvent): Unit = ()
        override def keyReleased(e: KeyEvent): Unit = ()
    
    class MyMouseListener extends com.jogamp.newt.event.MouseListener:
        override def mouseClicked(e: MouseEvent): Unit = ()
        override def mouseDragged(e: MouseEvent): Unit = ()
        override def mouseEntered(e: MouseEvent): Unit = ()
        override def mouseExited(e: MouseEvent): Unit = ()
        override def mouseMoved(e: MouseEvent): Unit = ()
        override def mousePressed(e: MouseEvent): Unit = ()
        override def mouseReleased(e: MouseEvent): Unit = ()
        override def mouseWheelMoved(e: MouseEvent): Unit = ()
        
    class MyWindowListener extends WindowListener:
        override def windowDestroyNotify(e: WindowEvent): Unit = ()
        override def windowGainedFocus(e: WindowEvent): Unit = ()
        override def windowDestroyed(e: WindowEvent): Unit = ()
        override def windowLostFocus(e: WindowEvent): Unit = ()
        override def windowMoved(e: WindowEvent): Unit = ()
        override def windowRepaint(e: WindowUpdateEvent): Unit = ()
        override def windowResized(e: WindowEvent): Unit = ()
    
    class MyGLEventListener extends GLEventListener:
        override def display(drawable: GLAutoDrawable): Unit = ()
        override def dispose(drawable: GLAutoDrawable): Unit = ()
        override def init(drawable: GLAutoDrawable): Unit = ()
        override def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int): Unit = ()

    class MyGestureHandler extends GestureHandler:
        override def clear(clearStarted: Boolean): Unit = ()
        override def getGestureEvent(): InputEvent = null
        override def hasGesture(): Boolean = false
        override def isWithinGesture(): Boolean = false
        override def process(e: InputEvent): Boolean = false
    
    class MySurfaceUpdatedListener extends SurfaceUpdatedListener:

        override def surfaceUpdated(updater: Object, ns: NativeSurface, when: Long): Unit = ()

    def init(startTimeMS: Long, isDebug: Boolean) = 

        if isDebug then
            window.getContext().addGLDebugListener(new MyDebugListener())
            window.getContext().enableGLDebugMessage(true)

        window.addGestureHandler            (new MyGestureHandler)  
        window.addGLEventListener           (new MyGLEventListener) 
        window.addKeyListener               (new MyKeyListener)
        window.addMouseListener             (new MyMouseListener)
        window.addSurfaceUpdatedListener    (new MySurfaceUpdatedListener)
        window.addWindowListener            (new MyWindowListener)


        window.setTitle("Battle3D")
        window.display()




        
        
