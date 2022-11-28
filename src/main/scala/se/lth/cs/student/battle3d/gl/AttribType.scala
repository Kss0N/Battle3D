package se.lth.cs.student.battle3d.gl


import com.jogamp.opengl.GL

/** Attribute Type
* @param val OpenGL value
*/
enum AttribType(val `val` : Int):
    case BYTE           extends AttribType(GL.GL_BYTE)
    case UNSIGNED_BYTE  extends AttribType(GL.GL_UNSIGNED_BYTE)
    case SHORT          extends AttribType(GL.GL_SHORT)
    case UNSIGNED_SHORT extends AttribType(GL.GL_UNSIGNED_SHORT)
    case UNSIGNED_INT   extends AttribType(GL.GL_UNSIGNED_INT)
    case FLOAT          extends AttribType(GL.GL_FLOAT)