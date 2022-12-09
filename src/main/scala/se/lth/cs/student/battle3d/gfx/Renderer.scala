package se.lth.cs.student.battle3d.gfx

import se.lth.cs.student.battle3d.main.Battle3D
import se.lth.cs.student.battle3d.util.Singleton
import se.lth.cs.student.battle3d.io.Logger
import se.lth.cs.student.battle3d.rsc.{
    Material,
    Mesh,
    Model,
    Sampler,
    Scene,
    Texture,
}
import se.lth.cs.student.battle3d.gl.{
    ElementBuffer,
    Shader,
    TextureBuffer,
    Topology,
    VertexArray,
    VertexBuffer,
}
import se.lth.cs.student.battle3d.gl.{
    AttribType,
}
import scala.collection.immutable.Stream.Empty

import jglm.{
    Jglm,
    Mat4,
    Vec3,
    Vec4,
}


import jglm.Mat

import org.lwjgl.opengl.{
    GL45,
    GL11,
    GL12,
    GL13,
    GL20,
    GLUtil
}
import org.lwjgl.system.{
    Callback, 
    MemoryUtil
}
import se.lth.cs.student.battle3d.gl.Shader.apply


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

private class MaterialAggregate(
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
    val mtl:        MaterialAggregate,
    val mode:       Topology = Topology.TRIANGLES,
    
    val indexCount: Int = 0,
    val eboType:    AttribType = AttribType.UNSIGNED_INT,
    val vertexCount:Int = 0
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

        val (ebo, eboCount, eboType) = mesh.indexAccessor match
            case None           => (None,0,AttribType.UNSIGNED_INT)
            case Some(indices)  =>
                val ebo = ElementBuffer()
                ebo.bufferSubData(
                    offset = 0,
                    size = indices.size,
                    data = indices.buffer.position(indices.offset.toInt)
                )
                (Some(ebo), indices.count.toInt, indices.`type`)

        val mtl = mesh.material match
            case None       => new MaterialAggregate(None,None,None,None,None) //TODO: Default texture
            case Some(mtl)  => MaterialAggregate(mtl)


        new MeshAggregate(
            vao,
            vbos,
            ebo,
            mtl,
            mesh.mode,

            eboCount,
            eboType,
            mesh.vertexAccessors(0).count
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

    var sceneGraph = collection.mutable.Map.empty[String, ModelAggregate]
    var running = false

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

    private var debugProc : Callback = null
    private val shaderPath = "src/rsc/shader/"
    private var defaultShader: Shader = null

    def parse(scene: Scene) : Unit = 
        sceneGraph = collection.mutable.Map.empty

        scene.models.foreach{ model =>
            Logger.printDebug("added model: " + model.name)
            sceneGraph += ((model.name, ModelAggregate(model)))
        }

    override def init(): Unit =
        var running = true //called first of all
        Display.associateThisThreadWithGL()
        debugProc = GLUtil.setupDebugMessageCallback();
        
        GL11.glViewport(0,0, Display.dim(0), Display.dim(1))
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthFunc(GL11.GL_LESS)

        defaultShader = Shader(shaderPath + "default.vert", shaderPath + "default.frag")


    override def destroy(): Unit = 
        //afterwards
        if debugProc != null then 
            debugProc.free()
        var running = false //called last of all

    def resizeWindow(newX: Int, newY: Int): Unit = 
        GL11.glViewport(0,0,newX, newY)

    def loop(): Unit =
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT)
        val camera : Mat4 = Camera.matrix

        sceneGraph.foreach{ (name,model)=>
            
            val matrix = model.matrix

            model.meshes.foreach{mesh => 
                mesh.vao.bind()


                defaultShader.activate()

                GL20.glUniformMatrix4fv(defaultShader.getUniformLocation("model"), false, matrix.toFloatArray())
                GL20.glUniformMatrix4fv(defaultShader.getUniformLocation("camera"), false, Camera.matrix.toFloatArray())
                
                
                mesh.ebo match
                    case None       => GL11.glDrawArrays(mesh.mode.get, 0, mesh.vertexCount)
                    case Some(value)=> GL11.glDrawElements(mesh.mode.get, mesh.indexCount, mesh.eboType.get, 0)
                
            }
            
        }
        Display.swapBuffers()
            
            
            
            
            

        