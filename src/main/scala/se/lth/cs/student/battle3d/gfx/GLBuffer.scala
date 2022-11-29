package se.lth.cs.student.battle3d.gfx

import se.lth.cs.student.battle3d.gl.Usage

import com.jogamp.opengl.GL4

import java.nio.Buffer

abstract class GLBuffer(val obj: Int)(using gl: GL4):
    val target: Int 

    def bufferData(size: Long, data: Buffer, usage: Usage = Usage(Usage.STATIC, Usage.DRAW)): Unit =
        gl.glNamedBufferData(obj, size, data, usage.`val`)
    
    def bufferSubData(offset: Long, size:Long, data:Buffer): Unit =
        gl.glNamedBufferSubData(obj, offset, size, data)
    
    def bind(): Unit =
        gl.glBindBuffer(target, obj)
