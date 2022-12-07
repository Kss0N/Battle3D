package se.lth.cs.student.battle3d.gfx


import se.lth.cs.student.battle3d.util.Singleton
import se.lth.cs.student.battle3d.io.Display
import se.lth.cs.student.battle3d.event.Event
import se.lth.cs.student.battle3d.rsc.{
    Material,
    Mesh,
    Model,
    Sampler,
    Scene,
    Texture,
}
import se.lth.cs.student.battle3d.gl.{
    AttribType,
}
import scala.collection.immutable.Stream.Empty

import jglm.{
    Mat4,
    Vec3,
    Vec4,
}

import se.lth.cs.student.battle3d.gl.{
    ElementBuffer,
    TextureBuffer,
    VertexArray,
    VertexBuffer,
}
import jglm.Mat

import org.lwjgl.opengl.{
    GL45,
    GL11,
    GL13,
    GL12,
}
import org.lwjgl.system.MemoryUtil


private val GL_TEXTURE0 = 0x84C0

private class TextureAggregate private(
    val tbo:    Int,
    val unit:   Int,
):
    //per opengl
    assert(unit < 32 + GL_TEXTURE0)
private object TextureAggregate:
    def apply(tex: Texture): TextureAggregate = 
        GL13.glActiveTexture(tex.coord + GL_TEXTURE0)
        val tbo = GL45.glCreateTextures(GL11.GL_TEXTURE_2D)

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, tex.minFilter)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, tex.magFilter)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, tex.wrapS)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, tex.wrapT)

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tex.imageWidth, tex.imageHeight, 0, GL11.GL_RGBA, AttribType.UNSIGNED_BYTE.get, MemoryUtil.memAddress(tex.buffer))

        new TextureAggregate(
            tbo, tex.coord + GL_TEXTURE0
        )
end TextureAggregate

private class MaterialAggregate private(
    val metallicRoughnessTexture: Option[TextureAggregate], 
    val baseColorTexture:         Option[TextureAggregate],
    val normals:                  Option[TextureAggregate], 
    val occlusion:                Option[TextureAggregate],
    val emissiveTexture:          Option[TextureAggregate],

    val normalScale:        Float   = 1.0f,
    val occlusionStrength:  Float   = 1.0f,
    val emissiveFactor:     Vec3    = Vec3(Array.fill[Float](3)(0)),
    val alphaMode:          String  = "OPAQUE",
    val alphaCutoff:        Float   = 0.5f,
    val doubleSided:        Boolean = false,

    val baseColorFactor:    Vec4    = Vec4(Array.fill[Float](4)(1)),
    val metallicFactor:     Float   = 1.0f,
    val roughnessFactor:    Float   = 1.0f, 
)
private object MaterialAggregate:
    def apply(material: Material): MaterialAggregate =
        new MaterialAggregate(
            metallicRoughnessTexture = (material.metallicRoughnessTexture match
                case None           => None
                case Some(texture)  => Some(TextureAggregate(texture))),
            baseColorTexture = (material.baseColorTexture match
                case None           => None 
                case Some(texture)  => Some(TextureAggregate(texture))),
            normals = (material.normals match
                case None           => None
                case Some(texture)  => Some(TextureAggregate(texture))),
            occlusion = (material.occlusion match
                case None           => None
                case Some(texture)  => Some(TextureAggregate(texture))),
            emissiveTexture = (material.emissiveTexture match
                case None           => None
                case Some(texture)  => Some(TextureAggregate(texture))),
            normalScale         = material.normalScale,
            occlusionStrength   = material.occlusionStrength,
            emissiveFactor      = material.emissiveFactor,
            alphaMode           = material.alphaMode,
            alphaCutoff         = material.alphaCutoff,
            doubleSided         = material.doubleSided,
            baseColorFactor     = material.baseColorFactor,  
            metallicFactor      = material.metallicFactor,
            roughnessFactor     = material.roughnessFactor 
        )
end MaterialAggregate

private class MeshAggregate private(
    val vao:        VertexArray,
    val vbo:        Vector[VertexBuffer], 
    val ebo:        Option[ElementBuffer], 
    val mtl:        MaterialAggregate
)

private object MeshAggregate:
    
    def apply(mesh: Mesh): MeshAggregate = 
        val vao = VertexArray()
        
        //Cut and paste all vertex accessors into new buffers
        val distinctBuffers = mesh.vertexAccessors.map{_.buffer}.distinct
        
        //TODO: implement other cases than StructOfArray
        val vbo = VertexBuffer()
        for ix <- mesh.vertexAccessors.indices do 
            val accessor = mesh.vertexAccessors(ix)

            vao.bindVertexBuffer(ix, vbo, accessor.offset.toInt, 0)
            vbo.bufferSubData(accessor.offset, accessor.size, accessor.buffer)
            
            vao.enableAttribute(ix)
            vao.setVertexAttribBinding(ix, 0)
            vao.setVertexAttribFormat(ix, accessor.size.toInt, accessor.`type`, accessor.normalized, accessor.offset.toInt)
        val vbos = Vector(vbo)

        val ebo = mesh.indexAccessor match
            case None           => None
            case Some(indices)  =>
                val ebo = ElementBuffer()
                ebo.bufferSubData(
                    offset = 0,
                    size = indices.size,
                    data = indices.buffer.position(indices.offset.toInt)
                )
                Some(ebo)
        //aggregate material


        new MeshAggregate(
            vao,
            vbos,
            ebo,
            null
        )

        


end MeshAggregate

private class  ModelAggregate(var matrix: Mat4, val meshes: Array[MeshAggregate])
private object ModelAggregate:
    def apply(model: Model): ModelAggregate = 
    new ModelAggregate(
        matrix = model.matrix,
        meshes = model.meshes.map{mesh => MeshAggregate(mesh)}.toArray
    )
end ModelAggregate

private class SceneAggegate

object Renderer extends Singleton:

    /** Aggregates and Replaces scenegraph with the newest one*/
    case class AggreggateScene(val scene: Scene) extends Event.Message

    /** adds a new Model Aggegate to scene*/
    case class AggregateAndAppendModel(val model: Model) extends Event.Message

    /** aggregates and upgrades a Mesh to a Model (with Unit Matrix as orgin)*/
    case class AggregateMeshAsModel(val mesh: Mesh) extends Event.Message

    /** for a certain model in the scene (given just the internal index as reference) replace it's matrix with a new one*/
    case class ReplaceMatrix(val model: Int) extends Event.Message

    var sceneGraph = collection.mutable.Map.empty[String, ModelAggregate]

    /**Generate unique identifier from name
      * 
      * For example: Model -> Model-0
      */
    def generateUID(name: String): String = 
        var myName: String = name
        var iteration = 0
        while sceneGraph.contains(myName) do
            myName = name + s"- $iteration"
        myName


    override def init(): Unit = ???

    override def destroy(): Unit = ???

    def loop: Unit =
        Display.associateThisThreadWithGL()
        while(Display.isRunning) do
            val queue = Event.getQueueNonBlocking("Renderer")
            if queue != None then 
                queue
                .get
                .dequeueAll{e => true} //everyone
                .foreach{ e =>
                    e match
                        case AggreggateScene(scene)         =>
                            sceneGraph.clear()
                            
                            scene.models.foreach{model =>
                                sceneGraph(model.name) = ???
                            }

                        case AggregateAndAppendModel(model) =>

                        case AggregateMeshAsModel(mesh)     =>

                        case ReplaceMatrix(model)           => 
                }
            //Onto Rendering:
            sceneGraph.foreach{ (name,model)=>

            }
            
            
            

        