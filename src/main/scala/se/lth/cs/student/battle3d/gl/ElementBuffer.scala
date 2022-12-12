package se.lth.cs.student.battle3d.gl


import org.lwjgl.opengl.{GL45 as GL4}

import se.lth.cs.student.battle3d.gl.Target

final case class ElementBuffer private(private[gl] val ebo: Int) extends GLBuffer(ebo):
    override val target: Target = Target.ELEMENT_ARRAY_BUFFER

object ElementBuffer:

    def apply(): ElementBuffer = 
        val buffers = Array.fill[Int](1)(0)
        GL4.glCreateBuffers(buffers)
        new ElementBuffer(buffers(0))