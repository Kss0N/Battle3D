package se.lth.cs.student.battle3d.gl

import com.jogamp.opengl.GL4
import se.lth.cs.student.battle3d.gl.Target

final case class ElementBuffer private(val ebo: Int)(using gl: GL4) extends GLBuffer(ebo):
    override val target: Target = Target.ELEMENT_ARRAY_BUFFER

object ElementBuffer:

    def apply()(using gl: GL4): ElementBuffer = 
        val buffers = Array.fill[Int](1)(0)
        gl.glCreateBuffers(1, buffers, 0)
        new ElementBuffer(buffers(0))