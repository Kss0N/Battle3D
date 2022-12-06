package se.lth.cs.student.battle3d.rsc

import jglm.{
    Vec3,
    Vec4
}

final class PBR(
    val baseColorFactor:            Vec4  = Vec4(Array.fill[Float](4)(1)),
    val baseColorTexture:           Texture,
    val metallicFactor:             Float = 1,
    val roughnessFactor:            Float = 1,
    val metallicRoughnessTexture:   Object
)

/** Describing textures and maps for a mesh.
  * 
  *
  * @param pbr               Optional Physically Based Rendering material 
  * @param normals           Optional Normal map. A Normal map is a replacement for vertex normals, instead of each vertex having a normal, the normal map enables the possibility for each pixel on each face to have a normal
  * @param occlusion         Optional map for the intencity of ambient lighting each pixel should receive (Blinn-Phong Lighting)
  * @param emissiveTexture   Optional map for the spectral intencity in Blinn-Phong Lighting
  * @param normalScale       Scaling of the normals. Normals should only be 1.0f in length
  * @param occlusionStrength Strength of the occlusion
  * @param emissiveFactor    
  * @param alphaMode
  * @param alphaCutoff
  * @param doubleSided
  */
final class Material(
    val pbr:            Option[PBR], 
    val normals:        Option[Texture], 
    val occlusion:      Option[Texture],
    val emissiveTexture:Option[Texture],

    val normalScale:    Float   = 1.0f,
    val occlusionStrength:Float = 1.0f,
    val emissiveFactor: Vec3    = Vec3(Array.fill[Float](3)(0)),
    val alphaMode:      String  = "OPAQUE",
    val alphaCutoff:    Float   = 0.5f,
    val doubleSided:    Boolean = false
)