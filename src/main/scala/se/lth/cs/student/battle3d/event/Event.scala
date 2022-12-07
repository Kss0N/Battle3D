package se.lth.cs.student.battle3d.event


import java.util.concurrent.locks.Lock

object Event:
    abstract class Message
    //This is used to send messages between threads. 
    //Each thread that needs to receive messages 
    //(because of some designated task like rendering because OpenGL is only available to one thread)
    //
    private val queues = scala.collection.mutable.Map.empty[String, (Lock, scala.collection.mutable.Queue[Message])]

    
    def getQueueNonBlocking(name: String): Option[scala.collection.mutable.Queue[Message]] = 
        if queues(name)(0).tryLock() then
            Some(queues(name)(1))
        else
            None
    
    def getQueue(name: String): scala.collection.mutable.Queue[Message] =
        while !queues(name)(0).tryLock() do ()
        queues(name)(1)

    def releaseQueue(name: String): Unit =
        queues(name)(0).unlock()

        