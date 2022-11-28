package se.lth.cs.student.battle3d.gfx

import com.jogamp.opengl.GL4
import com.jogamp.opengl.GL2
import com.jogamp.opengl.GL2ES1
import com.jogamp.opengl.GL2ES2

final case class VertexArray(val vao: Int)(using gl: GL4):

    def bindVertexBuffer(bindingIndex: Int, buffer: VertexBuffer, offset: Int, stride: Int): Unit = 
        gl.glVertexArrayVertexBuffer(vao, bindingIndex, buffer.vbo, offset, stride)
    
    def enableAttribute(attribIndex: Int): Unit = 
        gl.glEnableVertexArrayAttrib(vao, attribIndex)

    def setVertexAttribFormat(attribIndex: Int, size: Int, `type`: Int, normalized: Boolean = false, relativeoffset: Int = 0): Unit = 
        gl.glVertexArrayAttribFormat(vao, attribIndex, size, `type`, normalized, relativeoffset)

    def setVertexAttribBinding(attribIndex: Int, bindingIndex: Int): Unit = 
        gl.glVertexArrayAttribBinding(vao, attribIndex, bindingIndex)



object VertexArrays:
    def apply(n : Int)(using gl: GL4): Seq[VertexArray] = 
        val vertexArraysBuffer = Array.fill[Int](n)(0)
        gl.glCreateVertexArrays(n, vertexArraysBuffer, 0)
        vertexArraysBuffer.toSeq.map{vao => new VertexArray(vao)}


object VertexArray:
    object AttribType

    def apply()(using gl: GL4): VertexArray =
        VertexArrays(1)(0)