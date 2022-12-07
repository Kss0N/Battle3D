package se.lth.cs.student.battle3d.io

import org.json.{JSONObject, JSONException}
import java.io.{
    BufferedInputStream,
    FileInputStream,
    FileNotFoundException,
    InputStream,
    IOException,
}
import java.lang.UnsupportedOperationException
import java.nio.ByteBuffer
import de.matthiasmann.twl.utils.PNGDecoder
import se.lth.cs.student.battle3d.gl.{AttribType, Topology}
import se.lth.cs.student.battle3d.gfx.Renderer
import se.lth.cs.student.battle3d.rsc.{
    Accessor,
    Material,
    Mesh, 
    Model, 
    Sampler,
    Scene,
    Texture
}
import scala.util.{Try, Success, Failure}
import collection.mutable.ArrayBuffer
import jglm.{
    Jglm, 
    Mat4, 
    Quat, 
    Vec3,
    Vec4
}


private object Wrapper :

    //The following classes are taken from the GLTF reference page 
    //and are mainly intended to be used in this module as wrappers for the JSONObjects.
    //NOTE on when members are not found: 
    //      object members that MAY  be present and DO    have a default value, returns the default value (specified by the standard)
    //      object members that MAY  be present and DON'T have a default value, return `None`  
    //      object members that MAY  be present and DONT' have a default value and ARE (JSON) Arrays, return `Seq.empty`
    //      object members that MUST be present,will throw NoSuchElementException (annotated with an `@throws`)


    abstract class GLTFbase protected(protected val base: JSONObject)(using gltf: JSONObject):
        
        final protected def getGLTFObject(`type`: String): Try[JSONObject] = 
            Try {gltf.getJSONArray(
                `type` match
                    case "mesh" => "meshes"
                    case _      => `type` + "s"
            ).getJSONObject(base.getInt(`type`))}
            
        final protected def getGLTFObject(`type`: String, name: String, from: JSONObject = base): Try[JSONObject] = 
            Try {gltf.getJSONArray(
                `type`match 
                    case "mesh" => "meshes"
                    case _      => `type`+"s").getJSONObject(from.getInt(name))}
    object GLTFbase:

        def getGLTFObject(base: JSONObject, `type`: String, name: String = "")(using gltf: JSONObject): Try[JSONObject] =
            Try{gltf.getJSONArray(
                `type` match
                    case "mesh" => "meshes"
                    case _      => `type`+"s").getJSONObject(base.getInt(if name == "" then `type` else name))}
    end GLTFbase

    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-accessor
    class Accessor      (protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        
        def bufferView: Option[BufferView] = 
            getGLTFObject("bufferView") match 
                case Success(value) => Some(new BufferView(value))
                case Failure(_)     => None
            
        def byteOffset: Int =
            base.optInt("byteOffset", 0)
            
        @throws[NoSuchElementException]
        def componentType: Int = 
            try base.getInt("componentType") catch case _ => throw new NoSuchElementException("componentType is missing")
        
        def normalized: Boolean =
            base.optBoolean("normalized", false)
        
        @throws[NoSuchElementException]
        def `type`: String =
            try base.getString("type") catch case _ => throw new NoSuchElementException("type is missing")
            
        def max: Vector[Int] =
            Try{(0 until base.getJSONArray("max").length())
            .map{ ix=>base.getJSONArray("max").getInt(ix)}} match
                case Failure(exception) => Vector.empty
                case Success(value)     => value.toVector
        
        def min: Vector[Int] =
            Try{(0 until base.getJSONArray("min").length())
            .map{ ix =>base.getJSONArray("min").getInt(ix)}} match
                case Failure(exception) => Vector.empty
                case Success(value)     => value.toVector
    end Accessor
            
    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-buffer
    class Buffer        (protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        /** Size of entire file in bytes*/
        @throws[NoSuchElementException]
        def byteLength: Int = try base.getInt("byteLength") catch case _ => throw new NoSuchElementException("byteLength")
        
        def uri: String = 
            base.optString("uri","")
        
        def isInlineData: Boolean =
            //FIXME: make it totally inline with the standard
            Try{uri.take(5) == "data"} match
                case Failure(exception) => false
                case Success(value) => value
    end Buffer
            
    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-bufferview
    class BufferView    (protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
            
        @throws[NoSuchElementException]
        def buffer: Buffer =
            Buffer(getGLTFObject("buffer") match
                case Failure(exception) => throw new NoSuchElementException
                case Success(value)     => value)
        
        def byteOffset: Int = base.optInt("byteOffset", 0)
        
        @throws[NoSuchElementException]
        def byteLength: Int = 
            try {base.getInt("byteLength")} catch case _ => throw new NoSuchElementException("byteLength is missing")
        
        //TODO: See how functionality interacts with accessor and buffer
        def byteStride: Option[Int] = 
            Try{base.getInt("byteStride")} match
                case Failure(exception) => None
                case Success(value)     => Some(value)
        
        def target: Option[Int] = 
            Try{base.getInt("target")} match
                case Failure(exception) => None
                case Success(value)     => Some(value)
    end BufferView
    
    //see: https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-image
    class Image(protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        def uri: String = base.optString("uri", "")
        //Valid: (defined MimeType AND defined bufferView) XOR defined URI
        def mimeType: String = base.optString("mimeType", "")
        def bufferView: Option[BufferView] = getGLTFObject("bufferView") match
            case Failure(exception) => None
            case Success(value)     => Some(new BufferView(value))
    end Image
    
    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-material
    class Material      (protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        
        def pbrMetallicRoughness: Option[Material.PBRMetallicRoughness] = 
            getGLTFObject("pbrMetallicRoughness") match
                case Failure(exception) => None
                case Success(value)     => Some(new Material.PBRMetallicRoughness(value))

        def normalTexture: Option[Material.NormalTextureInfo] = 
            getGLTFObject("normalTextureInfo", "normalTexture") match
                case Failure(exception) => None
                case Success(value)     => Some(new Material.NormalTextureInfo(value))
        
        def occlusionTexture: Option[Material.OcclusionTextureInfo] = 
            getGLTFObject("occlusionTextureInfo", "occlusionTexture") match
                case Failure(exception) => None
                case Success(value)     => Some(new Material.OcclusionTextureInfo(value))
        def emissiveTexture: Option[TextureInfo] = 
            getGLTFObject("textureInfo", "emissiveTexture") match
                case Failure(exception) => None
                case Success(value)     => Some(new TextureInfo(value))
        def emissiveFactor: Vec3 =
            try Vec3{
                val arr = base.getJSONArray("emissiveFactor")
                (0 until 3)
                .map{ix => arr.optFloat(ix, 0.0f)}
                .toArray}
            catch case _ => Vec3(0,0,0)
        def alphaMode: String = 
            base.optString("alphaMode", "OPAQUE")
        def alphaCutoff: Float = 
            base.optFloat("alphaCutoff", 0.5f)
        def doubleSided: Boolean =
            base.optBoolean("doubleSided", false)
    object Material:
        class PBRMetallicRoughness(protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
            def baseColorFactor: Vec4 =
                try Vec4{
                    val arr = base.getJSONArray("baseColorFactor")
                    (0 until 4)
                    .map(ix => arr.optFloat(ix, 1.0f))
                    .toArray}
                catch case _ => Vec4(Array.fill[Float](4)(1.0f))

            def baseColorTexture: Option[Wrapper.TextureInfo] =
                getGLTFObject("textureInfo", "baseColorTexture") match
                    case Failure(_) => None
                    case Success(value) => Some(new TextureInfo(value))
                
            def metallicFactor: Float = 
                base.optFloat("metallicFactor", 1.0f)

            def roughnessFactor:Float = 
                base.optFloat("roughnessFactor",1.0f)

            def metallicRoughnessTexture: Option[TextureInfo] = 
                getGLTFObject("textureInfo", "metallicRoughnessTexture") match
                    case Failure(exception) => None
                    case Success(value)     => Some(new TextureInfo(value))

        class NormalTextureInfo(protected override val base: JSONObject)(using gltf: JSONObject) extends TextureInfo(base):
            def scale: Float = base.optFloat("scale", 1.0f)
        
        class OcclusionTextureInfo(protected override val base: JSONObject)(using gltf: JSONObject) extends TextureInfo(base):
            def strength: Float = base.optFloat("strength", 1.0f)
    end Material
            
    //see: https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-mesh
    class Mesh (override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        def name: String = 
            base.optString("name", "")

        def weights: Vector[Int] = 
            try 
                val array = base.getJSONArray("weights")
                (0 until array.length())
                .map{ix => array.getInt(ix) }
                .toVector
            catch
                case e: JSONException =>
                    Vector.empty

        @throws[NoSuchElementException]
        def primitives: Vector[Mesh.Primitive] = 
            try 
                val array = base.getJSONArray("primitives")
                (0 until array.length())
                .map{ix =>
                    new Mesh.Primitive(array.getJSONObject(ix))
                }
                .toVector
            catch 
                case _ => throw new NoSuchElementException(" Mesh primitives are missing")
    object Mesh:
        
        //see: https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-mesh-primitive
        class Primitive(protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
            
            @throws[NoSuchElementException]
            def attributes: JSONObject = 
                try base.getJSONObject("attributes") catch case _ => throw new NoSuchElementException("Mesh primitive attributes missing")

            def indices: Option[Wrapper.Accessor] = 
                getGLTFObject("accessor", "indices") match
                    case Failure(exception) => None 
                    case Success(value)     => Some(new Wrapper.Accessor(value))
                
            
            def material : Option[Wrapper.Material] = 
                getGLTFObject("material") match
                    case Failure(_)         => None 
                    case Success(material)  => Some(new Wrapper.Material(material))
                    
            def mode : Topology = 
                Try{base.getInt("mode")} match
                    case Success(value) => Topology.fromGL(value)
                    case Failure(_)     => Topology.TRIANGLES
    end Mesh
    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-node
    class Node  (protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
            
        //If the node carries TRS instead of a matrix, then translate them into a matrix
        //If both fails, return unit matix
        //[potential FIXME:] if there's discrepency in how it looks on the screen, then we'll transpose the matrix
        def matrix: Mat4 =
            def getFloatArray(name: String, default: Float): Array[Float] =
                (0 until base.getJSONArray(name).length())
                .map{ix => base.getJSONArray(name).optFloat(ix, default)}
                .toArray
            Try{getFloatArray("matrix", default=0)} match
                case Success(value) => Mat4(value)
                case Failure(_) =>
                    val translation = Try{getFloatArray("translation", 0.0f)} match
                        case Failure(_)     => Vec3(0,0,0)
                        case Success(values)=> Vec3(values)
                    //luckily Both GLM and GLTF uses [I,J,K,S] notation for quaternions
                    val rotation    = Try{getFloatArray("rotation", 0.0f)} match
                        case Failure(_)     => Quat(0,0,0,1)
                        case Success(arr)   => 
                            if arr.length >= 4 then Quat(arr(0),arr(1),arr(2),arr(3))
                            else ??? //TODO: Make Quaternions if array is corrupt
                    val scale       = Try{getFloatArray("scale", 1.0f)} match
                        case Failure(_)         => Vec3(1,1,1)
                        case Success(values)    => Vec3(values)

                    Mat4.translate(translation)
                    .mult(rotation.toMatrix())
                    .mult{
                        val matrix = new Mat4()
                        matrix.setDiagonal(scale)
                        matrix}                       
                    
        def children: Vector[Node] =  
            Try{
                val array = base.getJSONArray("children")
                (0 until array.length())
                .map{ix => new Node(gltf.getJSONArray("nodes").getJSONObject(array.getInt(ix)))}
            } match
                case Success(value) => value.toVector
                case Failure(_)     => Vector.empty[Node]
        
        def mesh: Option[Wrapper.Mesh] = 
            getGLTFObject("mesh") match
                case Failure(_)     => None
                case Success(value) => Some(new Wrapper.Mesh(value))
    end Node

    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-sampler
    class Sampler (protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        def magFilter: Option[Int] = Try{base.getInt("magFilter")} match
            case Failure(exception) => None
            case Success(value)     => Some(value)
        def minFilter: Option[Int] = Try{base.getInt("minFilter")} match
            case Failure(exception) => None
            case Success(value)     => Some(value)
        def wrapS: Int = base.optInt("wrapS", 10497)
        def wrapT: Int = base.optInt("wrapT", 10497)
    end Sampler
        
    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-scene
    class Scene(protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        def nodes: Vector[Wrapper.Node] = 
            (0 until base.getJSONArray("nodes").length())
            .map{ix => base.getJSONArray("nodes").getInt(ix)}
            .distinct
            .map {ix => gltf.getJSONArray("nodes").getJSONObject(ix)}
            .map{new Wrapper.Node(_)}
            .toVector
    end Scene

    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-texture
    class Texture(protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        def sampler: Option[Sampler] = getGLTFObject("sampler") match
            case Failure(exception) => None
            case Success(value)     => Some(new Sampler(value))
        @throws[NoSuchElementException] //NOTE: there are no extensions in place thus when failure, it's undefined
        def source: Image   = getGLTFObject("image", "source") match
            case Failure(exception) => throw new NoSuchElementException
            case Success(value)     => Image(value)
    end Texture
    
    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-textureinfo
    class TextureInfo(protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        
        @throws[NoSuchElementException]
        def index : Wrapper.Texture = Wrapper.Texture(getGLTFObject("texture", "index").get)
        
        def texture : Wrapper.Texture = index
        
        def texCoord: Int = base.optInt("texCoord", 0)     
    end TextureInfo
end Wrapper


/** Procedures for parsing GLTF streams
  * 
  */
object GLTF:

    @throws[FileNotFoundException]
    @throws[NoSuchElementException]
    private def parseAccessor(
            accessor:   Wrapper.Accessor, 
            name:       String, 
            buffers:    ArrayBuffer[(String, ByteBuffer)]   = ArrayBuffer.empty
    )(using gltf: JSONObject): se.lth.cs.student.battle3d.rsc.Accessor =
        val bufferView = Try{accessor.bufferView.get} match
            //NOTE: It is not an error if bufferView doesn't exist, it just means that the accessor is a sparse accessor
            //The sparse accessor should then be translated into a regular accessor if possible
            case Success(value)     => value
            case Failure(exception) => ??? //TODO: implement sparse accessors
        val buffer = bufferView.buffer
        //FIXME: It is not an error per standard if buffer uri does not exist, though that means something else which I haven't figured out yet.
        val data = 
            if buffer.isInlineData then 
                //FIXME: Do the `right` thing akchualliy
                Try {buffer.uri.drop("data:".length).map{_.toByte}.toArray } match
                    case Failure(_)      => ByteBuffer.wrap(Array.empty)
                    case Success(value)  => ByteBuffer.wrap(value)
            else//It's in an other file
                //Check for the possibility that the buffer already is recorded
                buffers.find((uri,buf)=> uri == buffer.uri) match
                    case None =>
                        val myBuffer    = 
                            if buffer.uri == "" then 
                                ByteBuffer.wrap(Array.empty)
                            else 
                                var bufferedStream : BufferedInputStream  = null 
                                try
                                    val fileStream  = new FileInputStream(buffer.uri)
                                    bufferedStream = new BufferedInputStream(fileStream)
                                    val buf = ByteBuffer.wrap(bufferedStream.readAllBytes())
                                    buf
                                finally
                                    bufferedStream.close()
                        buffers += ((buffer.uri, myBuffer))
                        myBuffer
                    case Some((uri, buffer)) => buffer
        //in bytes
        val stride : Int = bufferView.byteStride match
            case Some(value) => value
            case None        => 
                //It has to be calculated manually
                val componentSize = AttribType.byteSize(AttribType.fromGL(accessor.componentType))
                val typeSize = accessor.`type` match 
                    case "SCALAR"       => 1
                    case "VEC2"         => 2
                    case "VEC3"         => 3
                    case "VEC4"|"MAT2"  => 4
                    case "MAT3"         => 9
                    case "MAT4"         => 16   
                componentSize * typeSize                   
        new Accessor(
            name        = name, 
            buffer      = data, 
            offset      = accessor.byteOffset, 
            size        = bufferView.byteLength, 
            stride      = stride,
            `type`      = AttribType.fromGL(accessor.componentType),
            normalized  = accessor.normalized)
    

    private def parseGLTFsampler(
        sampler:        Wrapper.Sampler
    )(using gltf: JSONObject): se.lth.cs.student.battle3d.rsc.Sampler = 
        val magFiler = sampler.magFilter 
        val minFilter= sampler.minFilter
        new Sampler(
            magFilter = magFiler,
            minFilter = minFilter,
            wrapS = sampler.wrapS,
            wrapT = sampler.wrapT
        )


    @throws[NoSuchElementException]
    @throws[FileNotFoundException]
    private def parseGLTFtexture(
        textureInfo:    Wrapper.TextureInfo
    )(using gltf: JSONObject): se.lth.cs.student.battle3d.rsc.Texture = 
        val image   = textureInfo.texture.source
        val sampler = textureInfo.texture.sampler
        val coord   = textureInfo.texCoord

        //TODO: handle case when texture is in a bufferView
        val file    = new FileInputStream(image.uri)
        val stream  = new BufferedInputStream(file) 
        
        val decoder = PNGDecoder(stream)
        val buf     = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight())
        
        try 
            val buf = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight())
                
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA)
                buf.flip();
        catch 
            case e: IllegalArgumentException        => throw new NoSuchElementException
            case f: UnsupportedOperationException   => throw new NoSuchElementException
            case e: IOException                     => throw new FileNotFoundException
        finally
            stream.close()
        
        val magFiler = if sampler == None then 0x2601 else sampler.get.magFilter
        val minFilter= if sampler == None then 0x2601 else sampler.get.minFilter

        new Texture(
            buffer      = buf,
            coord       = coord,
            
            imageWidth  = decoder.getWidth,
            imageHeight = decoder.getHeight,

            magFilter   = if sampler == None || sampler.get.magFilter == None then 0x2601 else sampler.get.magFilter.get,
            minFilter   = if sampler == None || sampler.get.minFilter == None then 0x2601 else sampler.get.minFilter.get,
            wrapS = if sampler == None then 10497 else sampler.get.wrapS,
            wrapT = if sampler == None then 10497 else sampler.get.wrapT
        )
        

    @throws[NoSuchElementException]
    @throws[FileNotFoundException]
    private def parseGLTFmaterial(
        material:       Wrapper.Material
    )(using gltf: JSONObject): se.lth.cs.student.battle3d.rsc.Material = 
        val pbr = material.pbrMetallicRoughness
        val normals = material.normalTexture
        val occlusion=material.occlusionTexture
        val emissive= material.emissiveTexture

            
        new se.lth.cs.student.battle3d.rsc.Material(
            metallicRoughnessTexture = (pbr match
                case None       => None
                case Some(pbr)  => pbr.metallicRoughnessTexture match
                    case None           => None
                    case Some(texture)  => Some(parseGLTFtexture(texture))),
            baseColorTexture = (pbr match
                case None       => None
                case Some(pbr)  => pbr.baseColorTexture match
                    case None           => None
                    case Some(texture)  => Some(parseGLTFtexture(texture))),
            normals         = (normals match
                case None           => None
                case Some(normals)  => Some(parseGLTFtexture(normals))),
            occlusion       = (occlusion match
                case None           => None
                case Some(occlusion)=> Some(parseGLTFtexture(occlusion))),
            emissiveTexture = (emissive match
                case None           => None
                case Some(emissive) => Some(parseGLTFtexture(emissive))),

            
            normalScale         = if normals == None then 0 else normals.get.scale,
            occlusionStrength   = if occlusion==None then 0 else occlusion.get.strength,
            emissiveFactor      = material.emissiveFactor,
            alphaMode           = material.alphaMode,
            alphaCutoff         = material.alphaCutoff,
            doubleSided         = material.doubleSided,

            baseColorFactor  = if pbr == None then Vec4(Array.fill[Float](4)(1)) else pbr.get.baseColorFactor,
            metallicFactor   = if pbr == None then 1.0f                          else pbr.get.metallicFactor,
            roughnessFactor  = if pbr == None then 1.0f                          else pbr.get.roughnessFactor,
        )


    @throws[NoSuchElementException]
    @throws[FileNotFoundException]
    private def parseGLTFmesh(
        mesh:           Wrapper.Mesh
    )(using gltf: JSONObject): Vector[se.lth.cs.student.battle3d.rsc.Mesh] = 
        val b =0
        /** Persistant storage for all primitives: 
          * There is the (highly likely) possibility that multiple primitives are stored in the same buffer,
          * then it's a good idea to save them in the same buffer such that we do not need to have redundancies when it's time to parse
          * the GLTF primitives into Battle3D Meshes.
          */
        val buffers: ArrayBuffer[(String, ByteBuffer)] = ArrayBuffer.empty
    
        mesh.primitives
        //Translate GLTF.Mesh.Primitive -> Battle3D.Mesh 
        .map{primitive =>   
            //If this fails, the entire operation fails
            val attributes  = primitive.attributes
            val positionsAccessor = new Wrapper.Accessor(
                Wrapper.GLTFbase.getGLTFObject(attributes, "accessor", "POSITION") match
                    case Failure(_)     => throw new NoSuchElementException
                    case Success(value) => (value))

            //For each GLTF accessor, the buffer, the size, the offset, the stride and so on needs to be retrieved.
            val vertexAccessors = collection.mutable.ArrayBuffer.empty[se.lth.cs.student.battle3d.rsc.Accessor]
            val mode             = primitive.mode
            //Note: For the mesh primitive to be valid, there needs to be geometries;
            //it is totally possible to load a mesh that lacks any textures or normals;
            //if there are no normals, the lighting will appear all messed up 
            //And if there are no textures, then there might as well be some default texture.
            Wrapper.GLTFbase.getGLTFObject(attributes,"accessor","NORMAL") match
                case Failure(exception) => 
                case Success(accessor)  => 
                    vertexAccessors += (parseAccessor(new Wrapper.Accessor(accessor), "normals", buffers))
            Wrapper.GLTFbase.getGLTFObject(attributes,"accessor","TEXCOORD_0") match
                case Failure(exception) => 
                case Success(accessor)  => 
                    vertexAccessors += (parseAccessor(new Wrapper.Accessor(accessor), "textures", buffers))
            
            val indicesAccessor  = Wrapper.GLTFbase.getGLTFObject(mesh.base,"accessor","indices") match
                case Failure(exception) => None
                case Success(accessor)  => Some(parseAccessor(new Wrapper.Accessor(accessor), "indices"))
            val materials        = primitive.material
            
            vertexAccessors += (parseAccessor(positionsAccessor, "positions", buffers))
                
            Mesh(
                vertexAccessors.toSeq, 
                indicesAccessor,
                materials match
                    case None        => None
                    case Some(value) => Some(parseGLTFmaterial(materials.get)),
                mode
            )
        }
        .toVector


    @throws[NoSuchElementException]
    @throws[FileNotFoundException]
    private def parseToModel(
        mesh:           Wrapper.Mesh,
        matrix:         Mat4,
    )(using gltf: JSONObject): Model =
        val name = 
            val myName = mesh.name
            if(myName == "" || Renderer.sceneGraph.contains(myName)) then 
                Renderer.generateUID(if myName == "" then "model" else myName)
            else myName
        new Model(name, matrix, parseGLTFmesh(mesh))


    @throws[NoSuchElementException]
    @throws[FileNotFoundException]
    private def parseGLTFnode(
        node:           Wrapper.Node,
        parentMatrix:   Mat4, 
        gltfScene:      Wrapper.Scene, 
        myScene:        se.lth.cs.student.battle3d.rsc.Scene
    )(using gltf: JSONObject): Unit = 
            val matrix  = parentMatrix.mult(node.matrix)
            val mesh    = node.mesh
            if mesh != None then 
                myScene.models += parseToModel(mesh.get, matrix)
            node.children.foreach{node => parseGLTFnode(node, matrix, gltfScene, myScene)}


    //////////////////////////////////////////////////
    //                                              //
    //          PUBLIC      INTERFACES              //
    //                                              //
    //////////////////////////////////////////////////
    

    /** Parses data stream adhering to GLTF-standard to the models contained.
      * 
      * NOTE: `(legal JSON-syntax NAND legal GLTF-semantics) => parsing failure`
      *
      * @param data GLTF input stream
      * @return list of valid Models, all of them being positioned at orgin, being unrotated
      */
    @throws[Nothing]
    def parseModels(input: BufferedInputStream):    Vector[Model] =
        val beginTime = System.currentTimeMillis()
        try 
            given json: JSONObject = 
                try JSONObject(input.readAllBytes().map{_.toChar}.mkString) 
                catch case _ => throw new IllegalArgumentException("Illegal JSON-syntax in datastream")
            val meshes = 
                try json.getJSONArray("meshes") 
                catch case _ => throw new NoSuchElementException

            (0 until meshes.length())
            .map{ix => Try{meshes.getJSONObject(ix)} match
                case Failure(exception) => None
                case Success(mesh)      => Some(new Wrapper.Mesh(mesh))
            }
            .filter{_!=None}
            .map{mesh => parseToModel(mesh.get, new Mat4)} 
            .toVector
        catch 
            case e: IllegalArgumentException =>
                Logger.printError(e.getMessage() + ", parsing aborted")
                Vector.empty
            case e: NoSuchElementException  =>
                Logger.printError("Encountered an element that does not exist in source file, parsing aborted")
                Vector.empty
            case e: JSONException           =>
                Logger.printFatal("Unspecified error in GLTF parser:" + e.getMessage())
                Vector.empty
            case e: FileNotFoundException   =>
                Logger.printWarn("File currently not found, parsing aborted:" + e.getMessage())
                Vector.empty

    
    /** Parses data stream adhering to GLTF-standard to 1 or more scenes
      * 
      * NOTE: `(legal JSON-syntax NAND legal GLTF-semantics) => parsing failure`
      *
      * @param data GLTF input stream
      * @return Optional Scene that is decided as the first scene and all other scenes
      */
    @throws[Nothing]
    def parseScenes(input:    BufferedInputStream): (Option[Scene], Vector[Scene]) =
        val beginTie = System.currentTimeMillis()
        try 
            given json: JSONObject = try JSONObject(input.readAllBytes().map{_.toChar}.mkString) catch case _ => throw new IllegalArgumentException("Illegal JSON-syntax in data stream")
            val jsonScene = json.getJSONArray("scenes")

            val scenes = 
            (0 until jsonScene.length())
            .map{ix => Try{jsonScene.getJSONObject(ix)} match
                case Failure(_)     => None
                case Success(value) => Some(value)}
            .filter(_!=None)
            .map{json => 
                val wrapperScene = Wrapper.Scene(json.get)
                val scene = new Scene(ArrayBuffer.empty)
                wrapperScene.nodes.foreach{parseGLTFnode(_, new Mat4, wrapperScene, scene )}
                scene
            }
            .toVector
            
            //first to be viewed
            val scene = Try{scenes(json.getInt("scene"))} match
                case Success(scene) => Some(scene)
                case Failure(_)     => if !scenes.isEmpty then Some(scenes(0)) else None
            
            (scene, scenes)

        catch
            case e: IllegalArgumentException =>
                Logger.printError(e.getMessage() + ", parsing aborted")
                (None,Vector.empty)
            case e: NoSuchElementException  =>
                Logger.printError("Encountered an element that does not exist in source file, parsing aborted")
                (None,Vector.empty)
            case e: JSONException           =>
                Logger.printFatal("Unspecified error in GLTF parser:" + e.getMessage())
                (None,Vector.empty)
            case e: FileNotFoundException   =>
                Logger.printWarn("File currently not found, parsing aborted:" + e.getMessage())
                (None,Vector.empty)

