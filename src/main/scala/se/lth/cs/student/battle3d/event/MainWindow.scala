package se.lth.cs.student.battle3d.event

import com.jogamp.newt.opengl.GLWindow

import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLProfile

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL3
import com.jogamp.opengl.GL4

import com.jogamp.opengl.DebugGL
import com.jogamp.opengl.DebugGL2
import com.jogamp.opengl.DebugGL3
import com.jogamp.opengl.DebugGL4

object MainWindow:
    private var window: GLWindow = null 
    private var isDebug: Boolean = false


    def isFullScreen : Boolean = window.isFullscreen()
    def isVisible    : Boolean = window.isVisible()
    def dim : (Int, Int) = 
        (window.getWidth(), window.getHeight())

    def init(isDebug: Boolean): Unit = 
        MainWindow.isDebug = isDebug

        val profile = GLProfile.get(GLProfile.GL4)
        val capabilities = GLCapabilities(profile)
        window = GLWindow.create(capabilities)

        if isDebug then
            window.getContext().addGLDebugListener(new MyDebugListener())
            window.getContext().enableGLDebugMessage(true)
        
        window.addGestureHandler            (new MyGestureHandler)  
        window.addGLEventListener           (new MyGLEventListener) 
        window.addKeyListener               (new MyKeyListener)
        window.addMouseListener             (new MyMouseListener)
        window.addSurfaceUpdatedListener    (new MySurfaceUpdatedListener)
        window.addWindowListener            (new MyWindowListener)
        window.getScreen().addMonitorModeListener(new MyMonitorModeListener)
        
        window.setTitle("Battle3D")
        window.display()
        window.setFullscreen(true)
        window.setVisible(true)


    def destroy(): Unit = ???
    
    def getGL(version: Int = 4): GL = 
        val masterGL = window.getGL()
        val gl = version match 
            case 1 => masterGL.getGL()
            case 2 => masterGL.getGL2()
            case 3 => masterGL.getGL3()
            case 4 => masterGL.getGL4()
        if (isDebug) then 
            version match
                case 1 => throw new Exception("Error: There's no JOGL Debug for version 1")
                case 2 => DebugGL2(gl.asInstanceOf[GL2])
                case 3 => DebugGL3(gl.asInstanceOf[GL3])
                case 4 => DebugGL4(gl.asInstanceOf[GL4]) 
        else 
            gl
