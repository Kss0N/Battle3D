package se.lth.cs.student.battle3d.rsc

import se.lth.cs.student.battle3d.gl.AttribType
import se.lth.cs.student.battle3d.gl.Topology

import java.nio.ByteBuffer


/** Describes a buffer where data used by the renderer is stored
  * 
  * uses
  * 
  * @constructor        Accessor buffer
  * @param name         name of the datapoint
  * @param buffer       reference to buffer where data is stored
  * @param offset       offset into buffer where data is stored (bytes)
  * @param size         of data (in bytes)
  * @param stride       of each data point (in bytes)
  * @param type         dataType of data, default: Float
  * @param mode         (In case of vertices) mode of drawing
  * @param normalized   if data will be normalized, default: no
  */
final case class Accessor(
    val name        : String, 
    val buffer      : ByteBuffer, 
    val offset      : Long, 
    val size        : Long,
    val stride      : Int,
    val `type`      : AttribType = AttribType.FLOAT,
    val normalized  : Boolean = false
)
