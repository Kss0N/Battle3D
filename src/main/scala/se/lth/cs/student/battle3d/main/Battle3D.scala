package se.lth.cs.student.battle3d.main

import se.lth.cs.student.battle3d.io.{Logger, GLTF}
import se.lth.cs.student.battle3d.gfx.{Display,Renderer}

import java.io.{
    BufferedInputStream,
    FileInputStream
}

/** Main class and Entry point into the application
 * @author @Kss0N
 * 
 * 
*/
object Battle3D:

    val logger = Logger("logging.log")
    val isDebug = true 
    @volatile 
    var running = true
    var rendererRunnung = false
    
    def cleanup(): Unit = 
        Logger.printInfo("Shutting Down")
        running = false
        Renderer.destroy()
        Display.destroy()


    def main(args: Array[String]): Unit = 
        try
            Logger.printInfo("Starting Session")
            
            Logger.printDebug("reading configs")
            Configs.myConfigs = Configs.fromFile()
            
            Logger.printDebug("Init display")
            Display.init()
            Logger.printDebug("Init Rendering")

            Renderer.init()

            val (scene, scenes) = GLTF.parseScenes("src/rsc/models/mech_drone/scene.gltf")
            if scene == None then 
                Logger.printFatal("Failed to parse scene")
            else
                Logger.printDebug("Parsed Scene: " + scene.get.toString())
                
            Renderer.parse(scene.get)
            
            while Display.isRunning do 
                Renderer.loop()
                Display.poll()

            cleanup()
        finally
            Logger.destroy()
            Display.destroy()
            running = false
