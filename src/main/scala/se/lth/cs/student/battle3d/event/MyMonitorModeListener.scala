package se.lth.cs.student.battle3d.event

import com.jogamp.newt.event.{MonitorEvent, MonitorModeListener}

class MyMonitorModeListener extends MonitorModeListener:
    override def monitorModeChangeNotify(me: MonitorEvent): Unit = ()
    override def monitorModeChanged(me: MonitorEvent, success: Boolean): Unit = ()
