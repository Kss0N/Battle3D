package se.lth.cs.student.battle3d.render

import com.jogamp.opengl.DebugGL4
import com.jogamp.opengl.FBObject
import com.jogamp.opengl.DefaultGLCapabilitiesChooser
import com.jogamp.opengl.GL2GL3
import com.jogamp.opengl.GL2ES3
import com.jogamp.opengl.GL2ES2
import com.jogamp.opengl.GL4
import com.jogamp.opengl.GLBase
import com.jogamp.opengl.GLContext
import com.jogamp.opengl.GLCapabilities

import com.jogamp.opengl.GLPipelineFactory
import com.jogamp.opengl.GLProfile

import com.jogamp.opengl.util.Animator

import com.jogamp.opengl.util.GLBuffers

import com.jogamp.newt.opengl.GLWindow


import se.lth.cs.student.battle3d.io.Logger

import se.lth.cs.student.battle3d.main.Battle3D

import se.lth.cs.student.battle3d.event.MyDebugListener
import se.lth.cs.student.battle3d.event.MyGestureHandler
import se.lth.cs.student.battle3d.event.MyGLEventListener
import se.lth.cs.student.battle3d.event.MyKeyListener
import se.lth.cs.student.battle3d.event.MyMouseListener
import se.lth.cs.student.battle3d.event.MySurfaceUpdatedListener
import se.lth.cs.student.battle3d.event.MyWindowListener

object Renderer:
    val profile = GLProfile.get(GLProfile.GL4)
    val capabilities = GLCapabilities(profile)
    val window = GLWindow.create(capabilities)
    val myGL = window.getGL().getGL4bc()
    val gl = DebugGL4(myGL)

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




        
        

