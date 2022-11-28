package se.lth.cs.student.battle3d.event

import com.jogamp.newt.event.WindowEvent
import com.jogamp.newt.event.WindowListener
import com.jogamp.newt.event.WindowUpdateEvent

final class MyWindowListener extends WindowListener:
    override def windowDestroyNotify(e: WindowEvent): Unit = ()
    override def windowGainedFocus(e: WindowEvent): Unit = ()
    override def windowDestroyed(e: WindowEvent): Unit = ()
    override def windowLostFocus(e: WindowEvent): Unit = ()
    override def windowMoved(e: WindowEvent): Unit = ()
    override def windowRepaint(e: WindowUpdateEvent): Unit = ()
    override def windowResized(e: WindowEvent): Unit = ()