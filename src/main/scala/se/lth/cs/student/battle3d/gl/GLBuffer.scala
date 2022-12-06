package se.lth.cs.student.battle3d.gl

import se.lth.cs.student.battle3d.gl.{Usage, Target, GLval, AttribType}

//Some bindings don't carry to the latest
import org.lwjgl.opengl.{
  GL45 as GL,
  GL15,
  GL43}

import org.lwjgl.system.MemoryUtil

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.IntBuffer

/**Parent class of all OpenGL Buffer Object Wrapper types.
  * 
  * The wrapper implementation uses the `named` variants available since GL4 as far as possible to avoid unnecessary bind()s
  * 
  * see: 
  * https://www.khronos.org/opengl/wiki/Buffer_Object
  * for more details
  * 
  * @constructor     creates a GLBuffer object from an OpenGL object reference
  * @param obj       OpenGL object reference being wrapped
  */
abstract class GLBuffer protected(private val obj: Int):
    //NOTES:
    //LWJGL is as the name implies Light weight; so light weight that all gl*buffer* procedures require the pointer to 
    //the memadress we want to use.
    //for all data fields, the memAdress has to be retrieved
    import GLBuffer.*
    private def ptr(buffer: ByteBuffer): Long =
      MemoryUtil.memAddress(buffer)

    /** used when binding is necessary */
    val target: Target 

    /** Buffers Data of application to OpenGL driver.
      * After call, the driver decides where the data is located, it can either be in RAM (for CPU operations) or in GPU memory 
      * 
      * see 
      * https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferData.xhtml 
      * for more detail
      * @param size  of the buffer portion to be sent
      * @param data  to be buffered
      * @param usage hint to the driver on how the buffer is supposed be used in order to optimize performance
      */
    def bufferData(size: Long, data: ByteBuffer, usage: Usage = Usage(Usage.STATIC, Usage.DRAW)): Unit =
        GL.nglNamedBufferData(this.obj, size, MemoryUtil.memAddress(data), usage.`val`)

    /** Buffers part of data from application to OpenGL driver
      * After call, the driver decdes where the data is located, it can either be in RAM or GPU memory  
      *
      * see 
      * https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml
      * for more detail
      * 
      * @param offset into the buffer object to where the data will be transfered
      * @param size   of the block to transfer
      * @param data   to be transfered to
      */
    def bufferSubData(offset: Long, size:Long, data:ByteBuffer): Unit =
        GL.nglNamedBufferSubData(this.obj, offset, size, MemoryUtil.memAddress(data))   

    /** creates and initializes a buffer object's immutable data store 
      * 
      * see
      * https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferStorage.xhtml 
      * for more detail
      * 
      * @param size  of the buffer
      * @param data  to store
      * @param flags the intended usage of the buffer's data store.
      */
    def bufferStorage(size: Long, data: ByteBuffer, flags: Int): Unit =
        GL.nglNamedBufferStorage(obj, size, ptr(data), flags)

    /** Binds buffer to target buffer point */
    def bind(): Unit =
        GL15.glBindBuffer(target.get, obj)

    /** Binds buffer target point to 0, effectively not binding target to anything*/
    def unbind(): Unit =
        GL15.glBindBuffer(target.get, 0)

    /** copy all or part of the data store of a buffer object to the data store of another buffer object
      * 
      * see
      * https://registry.khronos.org/OpenGL-Refpages/gl4/html/glCopyBufferSubData.xhtml
      * for more detail
      *
      * @param writeBuffer  GLBuffer into which it's written
      * @param readOffset   Offset into `this` buffer from which data starts getting read
      * @param writeOffset  Offset into `writeBuffer` to which data starts getting written 
      * @param size         Of block to be copied
      */
    def copyBufferSubData(writeBuffer: GLBuffer, readOffset: Int, writeOffset: Int, size: Long): Unit =
        GL.glCopyNamedBufferSubData(obj, writeBuffer.obj, readOffset, writeOffset, size)

    /** fill a buffer object's data store with a fixed value
      * 
      * https://registry.khronos.org/OpenGL-Refpages/gl4/html/glClearBufferSubData.xhtml 
      * 
      * TODO: Document
      * @param internalFormat
      * @param format
      * @param type
      * @param data
      */
    def clearBufferData(internalFormat: Format, format: Format, `type`: AttribType, data: ByteBuffer): Unit =
        GL.nglClearNamedBufferData(obj, internalFormat.get, format.get, `type`.get, ptr(data))

    /**fill all or part of buffer object's data store with a fixed value
      * 
      * https://registry.khronos.org/OpenGL-Refpages/gl4/html/glClearBufferData.xhtml
      *
      * TODO: Document
      * FIXME: Formats
      * @param internalFormat   
      * @param offset
      * @param size
      * @param format
      * @param type
      * @param data
      */
    def clearBufferSubData(internalFormat: Format, offset: Long, size: Long, format: Format, `type`: AttribType, data: ByteBuffer): Unit =
        GL.nglClearNamedBufferSubData(obj, internalFormat.get, offset, size, format.get, `type`.get, ptr(data))

    /** returns a subset of a buffer object's data store (into a buffer)
      * 
      * https://registry.khronos.org/OpenGL-Refpages/gl4/html/glGetBufferSubData.xhtml
      *
      * @param offset   into the buffer object's data store from which data will be returned, measured in bytes.
      * @param size     in bytes of the data store region being returned.
      * @param data     the location where buffer object data is returned.
      */
    def getBufferSubData(offset: Long, size: Long, data: ByteBuffer): Unit =
        GL.nglGetNamedBufferSubData(obj, offset,size, ptr(data))

    /** returns a subset of a buffer objects' data store (as return value)
      * 
      * https://registry.khronos.org/OpenGL-Refpages/gl4/html/glGetBufferSubData.xhtml
      *
      * @param offset   into the buffer object's data store from which data will be returned, measured in bytes.
      * @param size     in bytes of the data store region being returned.
      * @return         data
      */
    def getBufferSubData(offset: Long, size: Long): Array[Byte]= 
        val buffer = ByteBuffer.wrap(Array.fill[Byte](size.toInt)(0))
        GL.nglGetNamedBufferSubData(obj, offset, size, ptr(buffer))
        buffer.array()
    
    /**
      * FIXME: 
      */
    def invalidateBufferData(): Unit =
        GL43.glInvalidateBufferData(obj)

    /**
      * FIXME:
      *
      * @param offset
      * @param length
      */
    def invalidateBufferSubData(offset: Long, length: Long): Unit = 
        GL43.glInvalidateBufferSubData(obj, offset, length)

    /** deletes buffer
      * 
      * After call, `this` GLBuffer instance is invalid
      */
    def delete(): Unit =
        val b = Array.fill[Int](1)(this.obj)
        GL15.glDeleteBuffers(IntBuffer.wrap(b))
    
    override def finalize(): Unit = this.delete()


object GLBuffer:

    //TODO: fix this
    enum Format private(`val`: Int) extends GLval(`val`):
        case R8 extends Format(0)
