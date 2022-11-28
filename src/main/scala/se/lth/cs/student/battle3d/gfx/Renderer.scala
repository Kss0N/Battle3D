package se.lth.cs.student.battle3d.gfx

import com.jogamp.opengl.DebugGL4
import com.jogamp.opengl.FBObject
import com.jogamp.opengl.DefaultGLCapabilitiesChooser
import com.jogamp.opengl.DebugGL2
import com.jogamp.opengl.DebugGL3
import com.jogamp.opengl.DebugGL4
import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL3
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLContext
import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLDrawable
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.GLPipelineFactory
import com.jogamp.opengl.GLProfile

import com.jogamp.opengl.util.Animator
import com.jogamp.opengl.util.GLBuffers

import com.jogamp.newt.opengl.GLWindow

import se.lth.cs.student.battle3d.io.Logger

import se.lth.cs.student.battle3d.main.Battle3D

import se.lth.cs.student.battle3d.event.MainWindow
import se.lth.cs.student.battle3d.event.MyDebugListener


final class Renderer(val isDebug: Boolean = false) extends GLEventListener:
    val shaders = collection.mutable.ArrayBuffer.empty[Shader]

    private def getGL(window: GLAutoDrawable, version: Int = 4): GL = 
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
    
    private def initPipeline(): Unit = ???



    //Procedure called every time we render something
    override def display(drawable: GLAutoDrawable): Unit = 
        given gl: GL = getGL(drawable).asInstanceOf[GL4]

    //Called at shutdown
    override def dispose(drawable: GLAutoDrawable): Unit =
        given gl: GL = getGL(drawable).asInstanceOf[GL4]

    //Called at startup
    override def init(drawable: GLAutoDrawable): Unit =
        if isDebug then
            drawable.getContext().addGLDebugListener(new MyDebugListener())
            drawable.getContext().enableGLDebugMessage(true)
        given gl: GL4 = getGL(drawable, 4).asInstanceOf[GL4]
        val (width,height) = MainWindow.dim

        gl.glViewport(0,0,width,height)
    
    //When resized or moved
    override def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int): Unit = 
        given gl: GL = getGL(drawable).asInstanceOf[GL4]
        gl.glViewport(0,0,width,height)

object Renderer

    

    




        
        

