package se.lth.cs.student.battle3d.gl

import org.lwjgl.opengl.{
    GL45 => GL,
    GL15,
    GL30,
    GL40,
    GL43,
    ARBVertexArrayObject => VAO
}


import se.lth.cs.student.battle3d.gl.AttribType

import java.nio.IntBuffer

/** OpenGL Vertex Array wrapper object
  * 
  * @constructor creates a VertexArray object from OpenGL Vertex Array Object Reference
  * @param vao   object reference, the real object is stored on the openGL side of the interface
  */
final case class VertexArray(private[gl] val vao: Int):
    import VertexArrays.* 

    def bind(): Unit =
        GL30.glBindVertexArray(vao) 

    def unbind(): Unit =
        GL30.glBindVertexArray(0)
    
    def bindVertexBuffer(bindingIndex: Int, buffer: VertexBuffer, offset: Int, stride: Int): Unit = 
        GL.glVertexArrayVertexBuffer(vao, bindingIndex, buffer.vbo, offset, stride)
    
    def enableAttribute(attribIndex: Int): Unit = 
        GL.glEnableVertexArrayAttrib(vao, attribIndex)
    
    def disableAttribute(attribIndex: Int): Unit =
        GL.glDisableVertexArrayAttrib(vao, attribIndex)

    def setVertexAttribFormat(attribIndex: Int, size: Int, `type`: AttribType = AttribType.FLOAT, normalized: Boolean = false, relativeoffset: Int = 0): Unit = 
        GL.glVertexArrayAttribFormat(vao, attribIndex, size, `type`.get, normalized, relativeoffset)

    def setVertexAttribBinding(attribIndex: Int, bindingIndex: Int): Unit = 
        GL.glVertexArrayAttribBinding(vao, attribIndex, bindingIndex)

    def delete(): Unit =
        val vertexArrays = Array.fill[Int](1)(vao)
        VAO.glDeleteVertexArrays(vertexArrays)

object VertexArrays:
    def apply(n : Int): Seq[VertexArray] = 
        val vertexArraysBuffer = Array.fill[Int](n)(0)
        GL.glCreateVertexArrays(vertexArraysBuffer)
        vertexArraysBuffer.toSeq.map{vao => new VertexArray(vao)}


object VertexArray:

    def apply(): VertexArray =
        VertexArrays(1)(0)