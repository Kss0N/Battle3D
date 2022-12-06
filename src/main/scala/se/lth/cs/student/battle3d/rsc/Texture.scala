package se.lth.cs.student.battle3d.rsc

import java.nio.ByteBuffer

class Texture(
    val sampler:Option[Sampler],
    val coord:  Int = 0, 
    val buffer: ByteBuffer
    )
