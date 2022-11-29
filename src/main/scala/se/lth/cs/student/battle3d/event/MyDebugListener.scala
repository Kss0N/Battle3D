package se.lth.cs.student.battle3d.event

import se.lth.cs.student.battle3d.main.Battle3D

import se.lth.cs.student.battle3d.io.Logger

import com.jogamp.opengl.{GL2ES2, GLDebugListener, GLDebugMessage}


final class MyDebugListener extends GLDebugListener:

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
