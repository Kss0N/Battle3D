package se.lth.cs.student.battle3d.gl


import org.lwjgl.opengl.GL45 as GL

import se.lth.cs.student.battle3d.gl.Target

import java.nio.Buffer

final case class VertexBuffer private(private[gl] val vbo: Int) extends GLBuffer(vbo):
    override val target = Target.ARRAY_BUFFER
    

object VertexBuffers

object VertexBuffer:

    def apply(): VertexBuffer = 
        val buffers = Array.fill[Int](1)(0)
        GL.glCreateBuffers(buffers)
        new VertexBuffer(buffers(0))
        


