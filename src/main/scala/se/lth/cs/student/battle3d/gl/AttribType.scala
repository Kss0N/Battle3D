package se.lth.cs.student.battle3d.gl


import com.jogamp.opengl.GL
import org.w3c.dom.Attr
import scala.util.Failure
import scala.util.Success

/** Attribute Type, typically used with `VertexArray.setVertexAttribFormat`
* @param val OpenGL value
*/
enum AttribType private (`val` : Int) extends GLval(`val`):
    case BYTE           extends AttribType(GL.GL_BYTE)
    case UNSIGNED_BYTE  extends AttribType(GL.GL_UNSIGNED_BYTE)
    case SHORT          extends AttribType(GL.GL_SHORT)
    case UNSIGNED_SHORT extends AttribType(GL.GL_UNSIGNED_SHORT)
    case UNSIGNED_INT   extends AttribType(GL.GL_UNSIGNED_INT)
    case FLOAT          extends AttribType(GL.GL_FLOAT)

object AttribType:

    /** finds the suitable AttribType from an OpenGL value */
    def fromGL(v: Int) = 
        util.Try {AttribType.values.filter(e =>  v == e.get)(0)} match
            case Failure(exception) => throw new IllegalArgumentException(s"Attribute Type can't be of value $v")
            case Success(value) => value
    
    def byteSize(e: AttribType) =
        e match
            //An other lucky bit from the KhronosGroup, the component type is equal to the GL_TYPE
            case AttribType.BYTE | AttribType.UNSIGNED_BYTE => 1
            case AttribType.SHORT| AttribType.UNSIGNED_SHORT=> 2
            case AttribType.FLOAT| AttribType.UNSIGNED_INT  => 4        
    