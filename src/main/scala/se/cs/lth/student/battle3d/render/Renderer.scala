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

class DebugListener extends GLDebugListener:

    def messageSent(event: GLDebugMessage) : Unit = ???
        event.getDbgSource() match

object Renderer:

    def init(): Unit = 
        val profile = GLProfile.get(GLProfile.GL4)
        val capabilities = GLCapabilities(profile)
        val window = GLWindow.create(capabilities)

        window.getContext().addGLDebugListener(new DebugListener())
        

