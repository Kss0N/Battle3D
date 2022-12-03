package se.lth.cs.student.battle3d.gl

import org.lwjgl.opengl.{
    GL,
    GL11,
    GL13,
    GL15,
    GL20,
    GL21,
    GL2ES3,
    GL3ES3,
    GL4
}


/** GL Buffer bind Target wrapper enum */
enum Target private(`val`: Int) extends GLval(`val`):
    case ARRAY_BUFFER               extends Target(0x8892)
    case ATOMIC_COUNTER_BUFFER      extends Target(0x92C0)
    case COPY_READ_BUFFER           extends Target(0x8F36)
    case COPY_WRITE_BUFFER          extends Target(0x8F37)
    case DISPATCH_INDIRECT_BUFFER   extends Target(0x90EE)
    case DRAW_INDIRECT_BUFFER       extends Target(0x8F3F)
    case ELEMENT_ARRAY_BUFFER       extends Target(0x8893)
    case PIXEL_PACK_BUFFER          extends Target(0x88EB)
    case PIXEL_UNPACK_BUFFER        extends Target(0x88EC)
    case QUERY_BUFFER               extends Target(0x9192)
    case SHADER_STORAGE_BUFFER      extends Target(0x90D2)
    case TEXTURE_BUFFER             extends Target(0x8C2A)
    case TRANSFORM_FEEDBACK_BUFFER  extends Target(0x8C8E)
    case UNIFORM_BUFFER             extends Target(0x8A11)