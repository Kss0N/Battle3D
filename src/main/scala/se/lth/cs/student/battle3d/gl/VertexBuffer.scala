package se.lth.cs.student.battle3d.gl


import com.jogamp.opengl.{GL, GL4}

import se.lth.cs.student.battle3d.gl.Target

import java.nio.Buffer

final case class VertexBuffer private(val vbo: Int)(using gl: GL4) extends GLBuffer(vbo):
    override val target = Target.ARRAY_BUFFER
    

object VertexBuffers

object VertexBuffer:

    def apply()(using gl: GL4): VertexBuffer = 
        val buffers = Array.fill[Int](1)(0)
        gl.glCreateBuffers(1, buffers, 0)
        new VertexBuffer(buffers(0))
        


