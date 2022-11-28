package se.lth.cs.student.battle3d.event


import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLEventListener

import se.lth.cs.student.battle3d.main
import se.lth.cs.student.battle3d.main.Battle3D
final class MyGLEventListener(val isDebug: Boolean) extends GLEventListener:

    //Procedure called every time we render something
    override def display(drawable: GLAutoDrawable): Unit = ()

    //Called at shutdown
    override def dispose(drawable: GLAutoDrawable): Unit = ()

    //Called at startup
    override def init(drawable: GLAutoDrawable): Unit =
        if isDebug then
            drawable.getContext().addGLDebugListener(new MyDebugListener())
            drawable.getContext().enableGLDebugMessage(true)


        Battle3D.isGLinitialized = true
        drawable.getGL().getGL4().glViewport(0,0,10,10)
    
    //When resized
    override def reshape(drawable: GLAutoDrawable, x: Int, y: Int, width: Int, height: Int): Unit = ()
