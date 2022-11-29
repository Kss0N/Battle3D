package se.lth.cs.student.battle3d.event

import com.jogamp.newt.event.{GestureHandler, InputEvent}

final class MyGestureHandler extends GestureHandler:
    override def clear(clearStarted: Boolean): Unit = ()
    override def getGestureEvent(): InputEvent = null
    override def hasGesture(): Boolean = false
    override def isWithinGesture(): Boolean = false
    override def process(e: InputEvent): Boolean = false
