package se.lth.cs.student.battle3d.event

import com.jogamp.nativewindow.{NativeSurface, SurfaceUpdatedListener}

class MySurfaceUpdatedListener extends SurfaceUpdatedListener:
    override def surfaceUpdated(updater: Object, ns: NativeSurface, when: Long): Unit = ()
