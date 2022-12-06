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
import jglm.Mat4
import se.lth.cs.student.battle3d.gl.{
    ElementBuffer,
    TextureBuffer,
    VertexArray,
    VertexBuffer,
}



private class MeshAggegate(
    val vao: VertexArray,
    val vbo: VertexBuffer, 
    val ebo: Option[ElementBuffer], 
    val tbo: Option[TextureBuffer])

private class ModelAggregate(var matrix: Mat4, val meshes: Array[MeshAggegate])

private class SceneAggegate

object Renderer extends Singleton:

    /** Aggregates and Replaces scenegraph with the newest one*/
    case class AggreggateScene(val scene: Scene) extends Event.Message

    /** adds a new Model Aggegate to scene*/
    case class AggregateAndAppendModel(val model: Model) extends Event.Message

    /** aggregates and upgrades a Mesh to a Model (with Unit Matrix as orgin)*/
    case class AggregateMeshAsModel(val mesh: Mesh) extends Event.Message

    /** for a certain model in the scene (given just the internal index as reference) replace it's matrix with a new one*/
    case class ReplaceMatrix(val model: Int) extends Event.Message

    var sceneGraph = collection.mutable.Map.empty[String, ModelAggregate]

    /**Generate unique identifier from name
      * 
      * For example: Model -> Model-0
      */
    def generateUID(name: String): String = 
        var myName: String = name
        var iteration = 0
        while sceneGraph.contains(myName) do
            myName = name + s"- $iteration"
        myName


    override def init(): Unit = ???

    override def destroy(): Unit = ???

    def loop: Unit =
        Display.associateThisThreadWithGL()
        while(Display.isRunning) do
            val queue = Event.getQueueNonBlocking("Renderer")
            if queue != None then 
                queue
                .get
                .dequeueAll{e => true} //everyone
                .foreach{ e =>
                    e match
                        case AggreggateScene(scene)         =>
                            sceneGraph.clear()
                            
                            scene.models.foreach{model =>
                                sceneGraph(model.name) = ???
                            }

                        case AggregateAndAppendModel(model) =>

                        case AggregateMeshAsModel(mesh)     =>

                        case ReplaceMatrix(model)           => 
                }
            //Onto Rendering:
            
            

        