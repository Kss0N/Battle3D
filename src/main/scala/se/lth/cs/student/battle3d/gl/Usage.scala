package se.lth.cs.student.battle3d.gl


/**from glad.h:
 * 
 * #define GL_STREAM_DRAW 0x88E0
 * #define GL_STREAM_READ 0x88E1
 * #define GL_STREAM_COPY 0x88E2
 * 
 * #define GL_STATIC_DRAW 0x88E4
 * #define GL_STATIC_READ 0x88E5
 * #define GL_STATIC_COPY 0x88E6
 * 
 * #define GL_DYNAMIC_DRAW 0x88E8
 * #define GL_DYNAMIC_READ 0x88E9
 * #define GL_DYNAMIC_COPY 0x88EA
 * 
*/
final case class Usage(val `val`: Int)


object Usage:
  object P:
    val STREAM  = 0
    val STATIC  = 4
    val DYNAMIC = 8
  object S:
    val DRAW    = 0
    val READ    = 1
    val COPY    = 2

  def apply(primary: Int, secondary: Int) = new Usage(0x88E0 + primary + secondary)