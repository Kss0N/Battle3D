package se.lth.cs.student.battle3d.event

import com.jogamp.newt.opengl.GLWindow


import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLContext
import com.jogamp.opengl.GLProfile

import com.jogamp.opengl.util.Animator

import se.lth.cs.student.battle3d.gfx.Renderer

object MainWindow:
    private var window: GLWindow = null 
    private var animator: Animator = null
    private var isDebug: Boolean = false

    def isFullScreen : Boolean = window.isFullscreen()
    def isVisible    : Boolean = window.isVisible()
    def dim : (Int, Int) = 
        (window.getWidth(), window.getHeight())

    def display() : Unit = window.display()

    def init(isDebug: Boolean): Unit = 
        MainWindow.isDebug = isDebug

        val profile = GLProfile.get(GLProfile.GL4)
        val capabilities = new GLCapabilities(profile)
        window = GLWindow.create(capabilities)
        window.setContextCreationFlags(GLContext.CTX_OPTION_DEBUG)

        window.setTitle("Battle3D")
        window.setFullscreen(true)
        window.setVisible(true)

        window.addGestureHandler            (new MyGestureHandler)  
        window.addGLEventListener           (new Renderer(isDebug)) 
        window.addKeyListener               (new MyKeyListener)
        window.addMouseListener             (new MyMouseListener)
        window.addSurfaceUpdatedListener    (new MySurfaceUpdatedListener)
        window.addWindowListener            (new MyWindowListener)
        window.getScreen().addMonitorModeListener(new MyMonitorModeListener)


    def destroy(): Unit = ???
    
    
