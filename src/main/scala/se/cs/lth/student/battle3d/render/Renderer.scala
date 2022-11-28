package se.cs.lth.student.battle3d.render

import com.jogamp.opengl.DebugGL4
import com.jogamp.opengl.FBObject
import com.jogamp.opengl.DefaultGLCapabilitiesChooser
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLBase
import com.jogamp.opengl.GLContext
import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLDebugListener
import com.jogamp.opengl.GLDebugMessage
import com.jogamp.opengl.GLPipelineFactory
import com.jogamp.opengl.GLProfile

import com.jogamp.opengl.util.GLBuffers

import com.jogamp.newt.opengl.GLWindow

import com.jogamp.opengl.GL2GL3
import com.jogamp.opengl.GL2ES3
import com.jogamp.opengl.GL2ES2

import se.lth.cs.student.battle3d.io.Logger
import se.lth.cs.student.battle3d.main.Battle3D

object Renderer:
    val profile = GLProfile.get(GLProfile.GL4)
    val capabilities = GLCapabilities(profile)
    val window = GLWindow.create(capabilities)
    val myGL = window.getGL().getGL4bc()
    val gl = DebugGL4(myGL)
    
    class DebugListener extends GLDebugListener:


        def messageSent(event: GLDebugMessage) : Unit =
            //see https://www.khronos.org/opengl/wiki/Debug_Output#Message_Components
            val severity = event.getDbgSeverity() match
                case GL2ES2.GL_DEBUG_SEVERITY_HIGH          => Logger.Severity.FATAL //All OpenGL errors should be regarded as unrecoverable
                case GL2ES2.GL_DEBUG_SEVERITY_MEDIUM        => Logger.Severity.WARN  //Medium indicates performance warnings, they do not cause direct problems therefore warnings are issued
                case GL2ES2.GL_DEBUG_SEVERITY_LOW           => Logger.Severity.DEBUG //Indicates redundancies, not a real problem
                case GL2ES2.GL_DEBUG_SEVERITY_NOTIFICATION|_=> Logger.Severity.INFO  //Anything else
            val source  = event.getDbgSource() match 
                case GL2ES2.GL_DEBUG_SOURCE_API             => "API"
                case GL2ES2.GL_DEBUG_SOURCE_APPLICATION     => "APPLICATION"
                case GL2ES2.GL_DEBUG_SOURCE_SHADER_COMPILER => "GLSL"
                case GL2ES2.GL_DEBUG_SOURCE_THIRD_PARTY     => "JOGL"
                case GL2ES2.GL_DEBUG_SOURCE_WINDOW_SYSTEM   => "Windows"
                case GL2ES2.GL_DEBUG_SOURCE_OTHER | _       => "Other"       
            //TODO: make use of this
            val `type`  = event.getDbgType() 
            val msg     = event.getDbgMsg()
            Battle3D.logger.newEntry(severity, "(OpenGL) "+ source+ ": " + msg)


    def init(): Unit = 


        window.getContext().addGLDebugListener(new DebugListener())
        

