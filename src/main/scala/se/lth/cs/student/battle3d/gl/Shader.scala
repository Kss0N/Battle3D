package se.lth.cs.student.battle3d.gl

import se.lth.cs.student.battle3d.io.Logger

import java.io.{FileInputStream, BufferedInputStream, StringReader}

import com.jogamp.opengl.{GL2ES2, GL4}

import com.jogamp.opengl.util.glsl.{
    ShaderCode,
    ShaderProgram,
    ShaderState,
    ShaderUtil,
}
import com.jogamp.opengl.util.glsl.fixedfunc.{FixedFuncUtil, ShaderSelectionMode}
import com.jogamp.opengl.util.glsl.sdk.{CompileShader, CompileShaderNVidia}
import java.io.IOException



final case class Shader private (val program: Int)(using gl: GL4):
    def destroy(): Unit = 
        gl.glDeleteProgram(program)
    def activate(): Unit = 
        gl.glUseProgram(program)
    //TODO: Implement
    //def isActive : Boolean =
    //
    //    gl.glGetInteger64v(GL3ES3.GL_CURRENT_PROGRAM)
    //    false
    override def finalize(): Unit = 
        this.destroy()
        


object Shader:
    //Will be set in the Renderer.init()

    private def makeShader(`type`: Int, paths: Seq[String])(using gl: GL4): ShaderCode = 
        val content : Seq[CharSequence] = paths.map{ path => 
            var bufferedInputStream: BufferedInputStream = null
            try
                val fileInputStream = new FileInputStream(path)
                bufferedInputStream = new BufferedInputStream(fileInputStream)
                bufferedInputStream.readAllBytes().toString()
            catch 
                case e: IOException => 
                    Logger.printFatal("File" + path + "Does not exist")
                    ""
            finally
                bufferedInputStream.close()
        }
        val shader = ShaderCode(`type`, content.length, Array(content.toArray))
        shader.compile(gl, null)
        shader

    @throws[IllegalArgumentException]
    def apply(paths: String*)(using gl: GL4): Shader=
        val vertPaths : Seq[String] = paths
            .filter(_ contains ".vert")
        val fragPaths : Seq[String] = paths
            .filter(_ contains ".frag")
        val geomPaths : Seq[String] = paths
            .filter(_ contains ".geom")

        if vertPaths.length == 0 || fragPaths.length == 0 then
            throw new IllegalArgumentException("To create a shader, there needs to be at least one vertex shader source and one fragment shader source")
        
        val vertShader = makeShader(GL2ES2.GL_VERTEX_SHADER, vertPaths)
        val fragShader = makeShader(GL2ES2.GL_FRAGMENT_SHADER, fragPaths)
        
        val program = ShaderProgram()
        program.add(vertShader)
        program.add(fragShader)
        program.link(gl, null)
        vertShader.destroy(gl)
        vertShader.destroy(gl)
        
        Shader(program.id())



        