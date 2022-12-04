package se.lth.cs.student.battle3d.rsc


/** Intermediate form to be send to the renderer where it gets aggregated
  * 
  * @param vertexAccessors  list of Accessors for vertex attributes for the VBO
  * @param indexAccessor    optional index accessor for the EBO
  * @param texture          optional texture, case none, use some default texture for aggregation
  */
final class Mesh(vertexAccessors: Seq[Accessor], indexAccessor: Option[Accessor], texture: Option[Texture]):
    assert(!vertexAccessors.isEmpty)  