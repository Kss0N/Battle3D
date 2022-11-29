package se.lth.cs.student.battle3d.gl


import com.jogamp.opengl.{GL, GL4}

import se.lth.cs.student.battle3d.gl.Target

import java.nio.Buffer

final case class VertexBuffer(val vbo: Int)(using gl: GL4) extends GLBuffer(vbo):
    override val target = Target.ARRAY_BUFFER
    

object VertexBuffers

object VertexBuffer


