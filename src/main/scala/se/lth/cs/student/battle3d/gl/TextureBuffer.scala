package se.lth.cs.student.battle3d.gl

final case class TextureBuffer private (val tbo: Int) extends GLBuffer(tbo):
    override val target: Target = Target.TEXTURE_BUFFER