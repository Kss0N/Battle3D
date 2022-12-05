package se.lth.cs.student.battle3d.gfx


import se.lth.cs.student.battle3d.util.Singleton
import se.lth.cs.student.battle3d.io.Display
import se.lth.cs.student.battle3d.event.Event
import se.lth.cs.student.battle3d.rsc.{
    Mesh,
    Model,
    Scene
}
import scala.collection.immutable.Stream.Empty

object Renderer extends Singleton:
    abstract class Message 

    /** Aggregates and Replaces scenegraph with the newest one*/
    case class AggreggateScene(val scene: Scene) extends Message

    /** adds a new Model Aggegate to scene*/
    case class AggregateAndAppendModel(val model: Model)

    /** aggregates and upgrades a Mesh to a Model (with Unit Matrix as orgin)*/
    case class AggregateMeshAsModel(val mesh: Mesh)

    /** for a certain model in the scene (given just the internal index as reference) replace it's matrix with a new one*/
    case class ReplaceMatrix(val model: Int)

    val sceneref = scala.collection.mutable.Map.empty[String, Long]
    private var scene = scala.collection.mutable.ArrayBuffer.empty[Byte]

    def sceneGraphContains(name: String) = sceneref.contains(name)

    /**Generate unique identifier from name
      * 
      * For example: Model -> Model-0
      */
    def generateUID(name: String): String = 
        var myName: String = name
        var iteration = 0
        while(sceneGraphContains(myName)) do
            myName = name + s"- $iteration"
        myName


    override def init(): Unit = ???

    override def destroy(): Unit = ???



    def loop: Unit =
        Display.associateThisThreadWithGL()
        while(Display.isRunning) do
            Event.getQueue("Renderer")
            .dequeueAll{e => true} //everyone
            .foreach{ e =>
                e match
                    case AggreggateScene(scene)         =>

                    case AggregateAndAppendModel(model) =>
                        
                    case AggregateMeshAsModel(mesh)     =>
                        
                        
                    case ReplaceMatrix(model)           => 
            }
            //Onto Rendering:

        