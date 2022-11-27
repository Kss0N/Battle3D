package se.cs.lth.student.battle3d.main

import se.lth.cs.student.battle3d.io.Logger

/** Main class and Entry point into the application
 * @author @Kss0N
 * 
 * 
*/
object Battle3D:
    val logger = Logger("Battle3D.log")

    def main(args: Array[String]): Unit = 
        Logger.printInfo("Starting Battle3D Session")
