package se.lth.cs.student.battle3d.rsc

final case class Sampler(
    val magFilter: Option[Int],
    val minFilter: Option[Int],
    val wrapS: Int,
    val wrapT: Int,
)
