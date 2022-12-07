package se.lth.cs.student.battle3d.rsc

import jglm.{
    Vec3,
    Vec4
}

/** Describes a material and maps it's surface features for a mesh
  * 
  *
  * @param pbrMetallicRoughnessTexture  Metallic Rougness Texture in case of Physically Based Rendering
  * @param pbrBaseColorTexture          Base Color Texture
  * @param normals                      Normal map (instead of using vertex normals each vertex, it's possible to map out a normal for each pixel)
  * @param occlusion                    Ambient Occlusion Texture
  * @param emissiveTexture              Spectral Emmision Texture
  * @param normalScale                  Global Scaling of Normal map (if normal map is used)
  * @param occlusionStrength            Global Strength of Ambient Occlusion (if occlusion maps are used)
  * @param emissiveFactor               Global Emmisivity factor of Spectral texture in RGB
  * @param alphaMode                              
  * @param alphaCutoff
  * @param doubleSided
  * @param pbrBaseColorFactor
  * @param pbrMetallicFactor
  * @param pbrRoughnessFactor
  */
final case class Material(
    val metallicRoughnessTexture: Option[Texture], 
    val baseColorTexture:         Option[Texture],
    val normals:                  Option[Texture], 
    val occlusion:                Option[Texture],
    val emissiveTexture:          Option[Texture],

    val normalScale:        Float   = 1.0f,
    val occlusionStrength:  Float   = 1.0f,
    val emissiveFactor:     Vec3    = Vec3(Array.fill[Float](3)(0)),
    val alphaMode:          String  = "OPAQUE",
    val alphaCutoff:        Float   = 0.5f,
    val doubleSided:        Boolean = false,

    val baseColorFactor: Vec4       = Vec4(Array.fill[Float](4)(1)),
    val metallicFactor:  Float      = 1.0f,
    val roughnessFactor: Float      = 1.0f,
)