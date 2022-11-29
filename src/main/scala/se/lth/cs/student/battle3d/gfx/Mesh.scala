package se.lth.cs.student.battle3d.gfx

import glm.vec._4.Vec4
import glm.vec._3.Vec3
import glm.vec._2.Vec2

import glm.vec._3.i.Vec3i

import scala.collection.mutable.ArrayBuffer

/**/

final class Mesh:
    val indices:    ArrayBuffer[Vec3i] = null
    val geometries: ArrayBuffer[Vec3]  = null
    val normals:    ArrayBuffer[Vec3]  = null
    val textures:   ArrayBuffer[Vec2]  = null
    /** index in the parent model for the material used */
    val material: Int=0