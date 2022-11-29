package se.lth.cs.student.battle3d.gfx

import com.jogamp.opengl.GL4
import se.lth.cs.student.battle3d.gl.Target

final case class ElementBuffer(val ebo: Int)(using gl: GL4) extends GLBuffer(ebo):
    override val target: Target = Target.ELEMENT_ARRAY_BUFFER