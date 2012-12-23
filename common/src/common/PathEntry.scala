package common

import java.awt.{BasicStroke, Color}

/**
 * Created by IntelliJ IDEA.
 * User: vijayannadi
 * Date: 12/15/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */

class PathEntry(val path: java.awt.geom.GeneralPath, val color: Color, val stroke: ScribbleStroke) extends Serializable {

  override def equals(other: Any) = other match {
    case that: PathEntry => that.canEqual(this) && this.path == that.path && this.color == that.color && this.stroke == that.stroke
    case _ => false
  }

  override
  def hashCode = 41 * (41 * (path.hashCode + 41) + color.hashCode) + stroke.hashCode

  override
  def toString = "PathEntry(" + path.toString + "," + color + "," + stroke + ")"

  def canEqual(that: PathEntry) = true
}

object PathEntry {
  val DEFAULT_STROKE = new ScribbleStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f)

  def apply(path: java.awt.geom.GeneralPath, color: Color) = new PathEntry(path, color, DEFAULT_STROKE)

  def apply(path: java.awt.geom.GeneralPath, color: Color, stroke: ScribbleStroke) = new PathEntry(path, color, stroke)

  // Extractor
  def unapply(pe: PathEntry) = Some((pe.path, pe.color, pe.stroke))
}

