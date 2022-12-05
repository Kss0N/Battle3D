package se.lth.cs.student.battle3d.main

import se.lth.cs.student.battle3d.io.{Logger, Display}
import se.lth.cs.student.battle3d.gfx.Renderer

/** Main class and Entry point into the application
 * @author @Kss0N
 * 
 * 
*/
object Battle3D:
    val logger = Logger("Battle3D.log")
    val isDebug = true 
    

    def main(args: Array[String]): Unit = 
        Logger.printInfo("Starting Battle3D Session")
        Display.init()
        Renderer.init()