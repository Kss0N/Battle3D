package se.lth.cs.student.battle3d.gfx


import com.jogamp.opengl.GL
import com.jogamp.opengl.GL4

import se.lth.cs.student.battle3d.gl.Target

import java.nio.Buffer

final case class VertexBuffer(val vbo: Int)(using gl: GL4) extends GLBuffer(vbo):
    val target = Target.ARRAY_BUFFER
    

object VertexBufers

object VertexBuffer


