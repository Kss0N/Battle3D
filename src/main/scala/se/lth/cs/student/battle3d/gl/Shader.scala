package se.lth.cs.student.battle3d.gl

import se.lth.cs.student.battle3d.io.Logger

import java.io.{
    FileInputStream, 
    BufferedInputStream, 
    StringReader,
    IOException
}
import java.nio.{
    IntBuffer
}

import org.lwjgl.opengl.GL20 as GL
import se.lth.cs.student.battle3d.io.Logger
import java.io.BufferedReader
import java.io.FileReader
import java.io.File



final case class Shader private (val program: Int):
    def destroy(): Unit = 
        GL.glDeleteProgram(program)
    def activate(): Unit = 
        GL.glUseProgram(program)
    //TODO: Implement
    //def isActive : Boolean =
    //
    //    gl.glGetInteger64v(GL3ES3.GL_CURRENT_PROGRAM)
    //    false
    override def finalize(): Unit = 
        this.destroy()
        


object Shader:

    private def makeShader(`type`: Int, paths: Seq[String]): Int = 
        val content : Seq[CharSequence] = paths.map{ path => 
            var reader: BufferedReader = null
            
            try
                val reader = new BufferedReader(new FileReader(new File(path)))
                var string = new java.lang.StringBuilder()
                var line : String = null
                while 
                    line = reader.readLine()
                    line != null
                do
                    string.append(line + "\n") 

                string.toString()

            catch 
                case e: IOException => 
                    Logger.printFatal("File" + path + "Does not exist")
                    ""
            finally
                if reader != null then
                    reader.close()
        }
        val shader = GL.glCreateShader(`type`)
        GL.glShaderSource(shader, content:_*)
        GL.glCompileShader(shader)

        val success = GL.glGetShaderi(shader, GL.GL_COMPILE_STATUS); 
        //0 is GL_FALSE meaning the compilation failed
        if success == 0 then
            val msg = GL.glGetShaderInfoLog(shader, 1024)
            Logger.printFatal(msg)
            println(msg)
        shader

    @throws[IllegalArgumentException]
    def apply(paths: String*): Shader=
        val vertPaths : Seq[String] = paths
            .filter(_ contains ".vert")
        val fragPaths : Seq[String] = paths
            .filter(_ contains ".frag")
        val geomPaths : Seq[String] = paths
            .filter(_ contains ".geom")

        if vertPaths.length == 0 || fragPaths.length == 0 then
            throw new IllegalArgumentException("To create a shader, there needs to be at least one vertex shader source and one fragment shader source")
        
        val vertShader = makeShader(GL.GL_VERTEX_SHADER, vertPaths)
        val fragShader = makeShader(GL.GL_FRAGMENT_SHADER, fragPaths)
        //TODO: All the other Shader types
        val geometryShader: Option[Int] = None
        
        val program = GL.glCreateProgram()
        GL.glAttachShader(program, vertShader)
        GL.glAttachShader(program, fragShader)
        
        if geometryShader != None then 
            GL.glAttachShader(program, geometryShader.get)

        GL.glLinkProgram(program)

        val success = GL.glGetProgrami(program, GL.GL_LINK_STATUS); 
        if success == 0 then //0 is GL_FALSE meaning the compilation failed
            val msg = GL.glGetProgramInfoLog(program, 1024)
            Logger.printFatal(msg)
        
        GL.glDeleteShader(vertShader)
        GL.glDeleteShader(fragShader)

        if geometryShader != None then 
            GL.glDeleteShader(geometryShader.get)

        new Shader(program)



        