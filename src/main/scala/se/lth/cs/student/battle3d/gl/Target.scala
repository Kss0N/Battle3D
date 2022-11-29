package se.lth.cs.student.battle3d.gl

import com.jogamp.opengl.{
    GL,
    GL2ES3,
    GL3ES3,
    GL4
}



enum Target private(`val`: Int) extends GLval(`val`):
    case ARRAY_BUFFER               extends Target(GL.GL_ARRAY_BUFFER)
    case ATOMIC_COUNTER_BUFFER      extends Target(GL2ES3.GL_ATOMIC_COUNTER_BUFFER)
    case COPY_READ_BUFFER           extends Target(GL2ES3.GL_COPY_READ_BUFFER)
    case COPY_WRITE_BUFFER          extends Target(GL2ES3.GL_COPY_WRITE_BUFFER)
    case DISPATCH_INDIRECT_BUFFER   extends Target(GL3ES3.GL_DISPATCH_INDIRECT_BUFFER)
    case DRAW_INDIRECT_BUFFER       extends Target(GL3ES3.GL_DRAW_INDIRECT_BUFFER)
    case ELEMENT_ARRAY_BUFFER       extends Target(GL.GL_ELEMENT_ARRAY_BUFFER)
    case PIXEL_PACK_BUFFER          extends Target(GL2ES3.GL_PIXEL_PACK_BUFFER)
    case PIXEL_UNPACK_BUFFER        extends Target(GL2ES3.GL_PIXEL_UNPACK_BUFFER)
    case QUERY_BUFFER               extends Target(GL4.GL_QUERY_BUFFER)
    case SHADER_STORAGE_BUFFER      extends Target(GL3ES3.GL_SHADER_STORAGE_BUFFER)
    case TEXTURE_BUFFER             extends Target(GL2ES3.GL_TEXTURE_BUFFER)
    case TRANSFORM_FEEDBACK_BUFFER  extends Target(GL2ES3.GL_TRANSFORM_FEEDBACK_BUFFER)
    case UNIFORM_BUFFER             extends Target(GL2ES3.GL_UNIFORM_BUFFER)