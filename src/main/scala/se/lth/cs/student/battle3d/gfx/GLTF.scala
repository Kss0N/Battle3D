package se.lth.cs.student.battle3d.gfx

import org.json.JSONObject
import org.json.JSONException

import java.io.IOException

import se.lth.cs.student.battle3d.gl.Topology

object GLTF:

    private class Accessor  (val base: JSONObject)(using gltf: JSONObject):

    end Accessor

    private class Buffer    (val base: JSONObject)(using gltf: JSONObject):

    end Buffer
    private class BufferView(val base: JSONObject)(using gltf: JSONObject):

    end BufferView


    /** Parses String as GLTF file, will only read the first mesh if any,
      * 
      * https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-mesh-primitive
      * 
      * @param data
      * @return new Mesh on success, None on failure
      */
    def parse(data: String): Option[Model] =
        try 
            given json: JSONObject = JSONObject(data)

  


            val mesh        = json.getJSONArray("meshes").getJSONObject(0)
            val primitives  = mesh.getJSONArray("primitives")



            //see https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html#reference-mesh-primitive
            (0 until primitives.length())
            .map{primitives.getJSONObject(_)}
            .foreach{primitive =>

                def getGLTFObject(`type`: String, name: String, from: JSONObject = primitive): Option[JSONObject] = 
                    try 
                        val accessorsIndex = from.getInt(name)
                        Some(json.getJSONArray(`type`+"s").getJSONObject(accessorsIndex))
                    catch 
                        case e: JSONException =>
                            None
                end getGLTFObject
                try 
                    //If this fails, the entire operation fails
                    val attributesObject = primitive.getJSONObject("attributes")
                    //a NoSuchElementException throw is warranted
                    val geometiesAccessor = getGLTFObject("accessor", "POSITION",    from = attributesObject).get 
                    
                    //Note: For the mesh primitive to be valid, there needs to be geometries;
                    //it is totally possible to load a mesh that lacks any textures or normals;
                    //if there are no normals, the lighting will appear all messed up 
                    //And if there are no textures, then there might as well be some default texture.
                    val normalsAccessor  = getGLTFObject("accessor", "NORMAL",      from = attributesObject)
                    val texturesAccessor = getGLTFObject("accessor", "TEXCOORD_0",  from = attributesObject)
                    val indicesAccessor  = getGLTFObject("accessor", "indices")
                    val materials        = getGLTFObject("material", "material")
                    val mode             = Topology.fromOrdinal(primitive.optInt("mode", Topology.TRIANGLES.get))
                catch 
                    case e: JSONException=>
                    case e: NoSuchElementException=>



            }


            Some(new Model)
        catch
            case e: JSONException=>
            case e: IOException=>
                
            None
    
    def parse(data: String*): Object = ???

