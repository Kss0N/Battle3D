package se.lth.cs.student.battle3d.gl

import org.lwjgl.opengl.GL45 as GL

final case class TextureBuffer private (private[gl] val tbo: Int) extends GLBuffer(tbo):
    override val target: Target = Target.TEXTURE_BUFFER

object TextureBuffer:
    def apply(): TextureBuffer = 
        val buffers = Array.fill[Int](1)(0)
        GL.glCreateBuffers(buffers)
        new TextureBuffer(buffers(0))