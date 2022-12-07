package se.lth.cs.student.battle3d.rsc

import java.nio.ByteBuffer

class Texture(
    val buffer: ByteBuffer,
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
    
    val coord:  Int = 0, 

    val magFilter:  Int = 0x2601, //Chosen by developer
    val minFilter:  Int = 0x2601,
    val wrapS:      Int = 10497,    //decided per standard
    val wrapT:      Int = 10497,
    )
