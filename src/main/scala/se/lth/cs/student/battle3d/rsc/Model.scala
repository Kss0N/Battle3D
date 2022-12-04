package se.lth.cs.student.battle3d.rsc

import jglm.Mat4

/** Model class encapsulating the objects meshes, as well as it's matrix (storing position and rotation)
  * 
  *
  * @param matrix
  * @param meshes
  */
final class Model(val name: String, val matrix: Mat4 = Mat4(), val meshes: Seq[Mesh])