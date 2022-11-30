package se.lth.cs.student.battle3d.gl

import com.jogamp.opengl.GL
import scala.util.Failure
import scala.util.Success

enum Topology(override val `val`:Int) extends GLval(`val`):
    case POINTS         extends Topology(GL.GL_POINTS)
    case LINES          extends Topology(GL.GL_LINES)
    case LINE_LOOP      extends Topology(GL.GL_LINE_LOOP)
    case LINE_STRIP     extends Topology(GL.GL_LINE_STRIP)
    case TRIANGLES      extends Topology(GL.GL_TRIANGLES)
    case TRIANGLE_STRIP extends Topology(GL.GL_TRIANGLE_STRIP)
    case TRIANGLE_FAN   extends Topology(GL.GL_TRIANGLE_FAN)

object Topology:

    /** selects enum value from openGL value*/
    def fromGL(v: Int): Topology =
        util.Try{Topology.values.filter(v == _.get)(0)} match
            case Failure(exception) => throw new java.lang.IllegalArgumentException(s"Value $v can't be used to construct enum Topology")
            case Success(value) => value