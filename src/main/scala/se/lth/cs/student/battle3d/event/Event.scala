package se.lth.cs.student.battle3d.event



object Event:
    //This is used to send messages between threads. 
    //Each thread that needs to receive messages 
    //(because of some designated task like rendering because OpenGL is only available to one thread)
    //
    private val queues = scala.collection.mutable.Map.empty[String, scala.collection.mutable.Queue[Object]]

    def getQueue(name: String) = queues(name)