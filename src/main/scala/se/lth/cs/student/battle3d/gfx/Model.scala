package se.lth.cs.student.battle3d.gfx

import glm.mat._4.Mat4


final class Model:
    var matrix : Mat4 = null
    val texture: Texture = null
    val mesh   : Mesh = null

object Model:

    def apply(path: String): Model = ???