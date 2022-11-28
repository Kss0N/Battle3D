package se.lth.cs.student.battle3d.gfx

import com.jogamp.opengl.GL4

import java.nio.Buffer

abstract class GLBuffer(val obj: Int)(using gl: GL4):
    val target: Int 

    def bufferData(size: Long, data: Buffer, usage: Int): Unit =
        gl.glNamedBufferData(obj, size, data, usage)
    
    def bufferSubData(offset: Long, size:Long, data:Buffer): Unit =
        gl.glNamedBufferSubData(obj, offset, size, data)
    
    def bind(): Unit =
        gl.glBindBuffer(target, obj)
