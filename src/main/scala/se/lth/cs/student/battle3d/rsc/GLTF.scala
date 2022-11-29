package se.lth.cs.student.battle3d.rsc

import org.json.JSONObject
import org.json.JSONException
import java.io.IOException

import se.lth.cs.student.battle3d.gl.Topology

object GLTF:


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

            

            val mesh        = json.getJSONArray("materials").getJSONObject(0)
            val primitives  = mesh.getJSONArray("primitives")
            
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

                //If this fails, the entire operation fails
                val attributesObject = primitive.getJSONObject("attributes")

                //It's okay if the following fails:
                val geometryAccessor = getGLTFObject("accessor", "POSITION",    from = attributesObject)
                val normalsAccessor  = getGLTFObject("accessor", "NORMAL",      from = attributesObject)
                val texturesAccessor = getGLTFObject("accessor", "TEXCOORD_0",  from = attributesObject)
                val indicesAccessor  = getGLTFObject("accessor", "indices")
                val materials        = getGLTFObject("material", "material")
                val mode             = Topology.fromOrdinal(primitive.optInt("mode", Topology.TRIANGLES.get))
             



            }


            Some(new Model)
        catch
            case e: JSONException=>
            case e: IOException=>
                
            None
    
    def parseAll(data: String): Object = ???

