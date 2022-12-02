package se.lth.cs.student.battle3d.gl

import com.jogamp.opengl.{GL,GL4}

import se.lth.cs.student.battle3d.gl.AttribType

import java.nio.IntBuffer

/** OpenGL Vertex Array wrapper object
  * 
  * @constructor creates a VertexArray object from OpenGL Vertex Array Object Reference
  * @param vao   object reference, the real object is stored on the openGL side of the interface
  */
final case class VertexArray(val vao: Int):
    import VertexArrays.* 

    def bind()(using gl: GL4): Unit =
        gl.glBindVertexArray(vao) 

    def unbind()(using gl: GL4): Unit =
        gl.glBindVertexArray(0)
    
    def bindVertexBuffer(bindingIndex: Int, buffer: VertexBuffer, offset: Int, stride: Int)(using gl: GL4): Unit = 
        gl.glVertexArrayVertexBuffer(vao, bindingIndex, buffer.vbo, offset, stride)
    
    def enableAttribute(attribIndex: Int)(using gl: GL4): Unit = 
        gl.glEnableVertexArrayAttrib(vao, attribIndex)
    
    def disableAttribute(attribIndex: Int)(using gl: GL4): Unit =
        gl.glDisableVertexArrayAttrib(vao, attribIndex)

    def setVertexAttribFormat
    (attribIndex: Int, size: Int, `type`: AttribType = AttribType.FLOAT, normalized: Boolean = false, relativeoffset: Int = 0)(using gl: GL4): Unit = 
        gl.glVertexArrayAttribFormat(vao, attribIndex, size, `type`.get, normalized, relativeoffset)

    def setVertexAttribBinding(attribIndex: Int, bindingIndex: Int)(using gl: GL4): Unit = 
        gl.glVertexArrayAttribBinding(vao, attribIndex, bindingIndex)

    def delete()(using gl: GL4): Unit =
        val vertexArrays = Array.fill[Int](1)(vao)
        gl.glDeleteVertexArrays(1, IntBuffer.wrap(vertexArrays))

object VertexArrays:
    def apply(n : Int)(using gl: GL4): Seq[VertexArray] = 
        val vertexArraysBuffer = Array.fill[Int](n)(0)
        gl.glCreateVertexArrays(n, vertexArraysBuffer, 0)
        vertexArraysBuffer.toSeq.map{vao => new VertexArray(vao)}


object VertexArray:

    def apply()(using gl: GL4): VertexArray =
        VertexArrays(1)(0)