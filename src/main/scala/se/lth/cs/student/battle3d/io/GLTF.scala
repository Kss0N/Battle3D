package se.lth.cs.student.battle3d.io

import org.json.{JSONObject, JSONException}

import java.io.{IOException,FileInputStream,BufferedInputStream}

import java.nio.ByteBuffer

import se.lth.cs.student.battle3d.gl.{AttribType, Topology}
import se.lth.cs.student.battle3d.gfx.Renderer
import se.lth.cs.student.battle3d.rsc.{
    Accessor => Accessor,
    Mesh, 
    Model, 
    Scene
}

import scala.util.{Try, Success, Failure}

import collection.mutable.ArrayBuffer

import jglm.{
    Jglm, 
    Mat4, 
    Quat, 
    Vec3}
import java.io.FileNotFoundException


//Private so it's only visible in this package
/**
  * 
  *
  * @param a
  * @param name     
  * @param mode     (used for vertex accessors)
  * @param buffers Since there's a high chance that multiple accessors reference the same buffer, it's a good idea to cache all buffers so in the (likely) event that accessors reference the same buffer, it saves time by bypassing multiple levels of OS-calls
  * @return
  */
@throws[FileNotFoundException]
@throws[NoSuchElementException]
private def parseAccessor(
        accessor:   Wrapper.Accessor, 
        name:       String, 
        mode:       Topology = Topology.TRIANGLES, 
        buffers:    ArrayBuffer[(String, ByteBuffer)] = ArrayBuffer.empty
    ): Accessor =

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
                    val fileStream  = new FileInputStream(buffer.uri)
                    val bufferedStream  = new BufferedInputStream(fileStream)
                    val myBuffer    = ByteBuffer.wrap(bufferedStream.readAllBytes())
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
        mode        = mode,
        normalized  = accessor.normalized)
end parseAccessor



private object Wrapper :
    abstract class GLTFbase protected(protected val base: JSONObject)(using gltf: JSONObject):
        protected def getGLTFObject(`type`: String): Option[JSONObject] = 
            Try {gltf.getJSONArray(
                `type` match
                    case "mesh" => "meshes"
                    case _      => `type` + "s"
            ).getJSONObject(base.getInt(`type`))} match
                case Success(value)     => Some(value)
                case Failure(exception) => None
            
        protected def getGLTFObject(`type`: String, name: String, from: JSONObject = base): Option[JSONObject] = 
            Try {
                gltf.getJSONArray(`type`match 
                    case "mesh" => "meshes"
                    case _      => `type`+"s").getJSONObject(from.getInt(name))
            } match
                case Success(value) => Some(value)
                case Failure(_)     => None
    //The following classes are taken from the GLTF reference page 
    //and are mainly intended to be used in this module as wrappers for the JSONObjects.
    //NOTE on when members are not found: 
    //      object members that MAY  be present and DO    have a default value, returns the default value (specified by the standard)
    //      object members that MAY  be present and DON'T have a default value, return `None`  
    //      object members that MAY  be present and DONT' have a default value and ARE (JSON) Arrays, return `Seq.empty`
        //      object members that MUST be present,will throw NoSuchElementException (annotated with an `@throws`)
    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-accessor
    class Accessor      (protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
        def bufferView: Option[BufferView] = 
            util.Try{getGLTFObject("bufferView").get} match 
                case util.Success(obj: JSONObject) => Some(new BufferView(obj))
                case util.Failure(e)=> None
            
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
            util.Try{(0 until base.getJSONArray("max").length())
            .map{ ix=>base.getJSONArray("max").getInt(ix)}} match
                case Failure(exception) => Vector.empty
                case Success(value)     => value.toVector
        
        def min: Vector[Int] =
            util.Try{(0 until base.getJSONArray("min").length())
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
            Try{base.getString("uri")} match
                case Failure(exception) => ""
                case Success(value)     => value
        
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
            Wrapper.Buffer(getGLTFObject("buffer").get)
        
        def byteOffset: Int = base.optInt("byteOffset", 0)
        
        @throws[NoSuchElementException]
        def byteLength: Int = 
            try {base.getInt("byteLength")} catch case _ => throw new NoSuchElementException("byteLength is missing")
        
        //TODO: See how functionality interacts with accessor and buffer
        def byteStride: Option[Int] = 
            util.Try{base.getInt("byteStride")} match
                case Failure(exception) => None
                case Success(value) => Some(value)
        
        def target: Option[Int] = 
            util.Try{base.getInt("target")} match
                case Failure(exception) => None
                case Success(value) => Some(value)
    end BufferView
    
    //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-node
    class Node  (protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
            
        //If the node carries TRS instead of a matrix, then translate them into a matrix
        //If both fails, return unit matix
        //[potential FIXME:] if there's discrepency in how it looks on the screen, then we'll transpose the matrix
        def matrix: Mat4 =
            def getFloatArray(name: String): Array[Float] =
                (0 until base.getJSONArray(name).length())
                .map{ix => base.getJSONArray(name).getFloat(ix)}
                .toArray
            Try{getFloatArray("matrix")} match
                case Success(value) => Mat4(value)
                case Failure(_) =>
                    val translation = Try{getFloatArray("translation")} match
                        case Failure(_)     => Vec3(0,0,0)
                        case Success(values)=> Vec3((0 until 3).indices.map{ix=> Try{values(ix)} match
                            case Failure(exception) => 0.0f
                            case Success(value) => value
                        }.toArray)
                    //luckily Both GLM and GLTF uses [I,J,K,S] notation for quaternions
                    val rotation    = Try{getFloatArray("rotation")} match
                        case Failure(_)     => Quat(0,0,0,1)
                        case Success(arr)   => 
                            if arr.length >= 4 then Quat(arr(0),arr(1),arr(2),arr(3))
                            else ??? //TODO: Make Quaternions if array is corrupt
                    val scale       = Try{getFloatArray("scale")} match
                        case Failure(_)         => Vec3(1,1,1)
                        case Success(values)    => Vec3((0 until 3).indices.map{ix=> Try{values(ix)} match
                            case Failure(exception) => 0.0f
                            case Success(value) => value
                        }.toArray)

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
        
        def meshName: String = 
            base.getJSONObject("mesh").optString("name", "") //[""] may be considered an empty sequence
        
        //Returns the Node's  GLTF Mesh primitives (getting translated to Battle3D meshes) (if any),  
        def meshes: Vector[Mesh] = 
            val obj = getGLTFObject("mesh").get
            val primitives = obj.getJSONArray("primitives")
            // Persistant storage for all primitives: 
            // There is the (highly likely) possibility that multiple primitives are stored in the same buffer, 
            // then it's a good idea to save them in the same buffer such that we do not need to have redundancies when it's time to parse
            // the GLTF primitives into Battle3D Meshes.
            val buffers: collection.mutable.ArrayBuffer[(String, ByteBuffer)] = ArrayBuffer.empty
            //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-mesh-primitive
            (0 until primitives.length)
            .map{ix => primitives.getJSONObject(ix)}
            //Translate GLTF.Mesh.Primitive -> Battle3D.Mesh 
            .map{primitive =>   
                //If this fails, the entire operation fails
                val attributesObject  = primitive.getJSONObject("attributes")
                val positionsAccessor = new Accessor(getGLTFObject("accessor", "POSITION",   from = attributesObject).get) //warranted NoSuchElementException on failure
                //Note: For the mesh primitive to be valid, there needs to be geometries;
                //it is totally possible to load a mesh that lacks any textures or normals;
                //if there are no normals, the lighting will appear all messed up 
                //And if there are no textures, then there might as well be some default texture.
                val normalsAccessor  = getGLTFObject("accessor", "NORMAL",      from = attributesObject)
                val texturesAccessor = getGLTFObject("accessor", "TEXCOORD_0",  from = attributesObject)
                val indicesAccessor  = getGLTFObject("accessor", "indices")
                val materials        = getGLTFObject("material", "material")
                val mode             = Try{Topology.fromGL(primitive.getInt("mode"))} match
                    case Success(value) => value
                    case Failure(_)     => Topology.TRIANGLES
                //For each GLTF accessor, the buffer, the size, the offset, the stride and so on needs to be retrieved.
                val vertexAccessors = collection.mutable.ArrayBuffer.empty[se.lth.cs.student.battle3d.rsc.Accessor]
                vertexAccessors += (parseAccessor(positionsAccessor, "positions", mode, buffers))
                if normalsAccessor != None then 
                    vertexAccessors += (parseAccessor(new Wrapper.Accessor(normalsAccessor.get), "normals", mode))
                if texturesAccessor != None then
                    vertexAccessors += (parseAccessor(new Wrapper.Accessor(texturesAccessor.get), "textures", mode))
                
                Mesh(
                    vertexAccessors.toSeq, 
                    indicesAccessor match
                        case None => None
                        case Some(accessor) => Some(parseAccessor(new Wrapper.Accessor(accessor), "indices", mode)),
                    //TODO: Add materials
                    None
                )
            }
            .toVector
    end Node

        //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-scene
    class Scene(protected override val base: JSONObject)(using gltf: JSONObject) extends GLTFbase(base):
            def nodes: Vector[Wrapper.Node] = 
                (0 until base.getJSONArray("nodes").length())
                .map{ix => base.getJSONArray("nodes").getInt(ix)}
                .distinct
                .map {ix => gltf.getJSONArray("nodes").getJSONObject(ix)}
                .map{
                    new Wrapper.Node(_)
                }.toVector
    end Scene
    
    






object GLTF:

    /**
      * 
      *
      * @param node
      * @param parentMatrix
      * @param gltfScene
      * @param myScene
      * @param gltf
      */
    private def parseNode(
        node:           Wrapper.Node, 
        parentMatrix:   Mat4, 
        gltfScene:      Wrapper.Scene, 
        myScene:        Scene)
        (using gltf: JSONObject): Unit =
        
        val matrix = parentMatrix.mult(node.matrix)
        val meshes = node.meshes
        if !meshes.isEmpty then 
            val name = 
                val myName = node.meshName
                if(myName == "" || Renderer.sceneGraphContains(myName)) then 
                    Renderer.generateUID(if myName == "" then "model" else myName)
                else myName
            myScene.models += new Model(name, matrix, meshes)
        node.children.foreach{parseNode(_, matrix, gltfScene, myScene)}
    


    def parse(data: String): (Option[Scene], Vector[Scene]) =
        try 
            given json: JSONObject = JSONObject(data)

            val scenes = (0 until json.getJSONArray("scenes").length())
            .map{ix => Wrapper.Scene(json.getJSONArray("scenes").getJSONObject(ix))}
            .map{ wrapperScene => 
                val scene = new Scene(ArrayBuffer.empty)
                wrapperScene.nodes.foreach{parseNode(_, new Mat4, wrapperScene, scene )}
                scene
            }

            val mesh        = json.getJSONArray("meshes").getJSONObject(0)
            val primitives  = mesh.getJSONArray("primitives")

            (Some(scenes(0)), scenes.toVector)
        catch
            case e: NoSuchElementException  =>
                Logger.printError("Encountered an element that does not exist in source file, parsing aborted")
                (None,Vector.empty)
            case e: JSONException           =>
                Logger.printFatal("Unspecified error in GLTF parser:" + e.getMessage())
                (None,Vector.empty)
            case e: FileNotFoundException   =>
                Logger.printWarn("File currently not found, parsing aborted:" + e.getMessage())
                (None,Vector.empty)
                
    def parse(data: String*): Object = ???

