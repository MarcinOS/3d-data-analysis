package common

import java.awt.Color

/**
 * Created by IntelliJ IDEA.
 * User: vijayannadi
 * Date: 12/15/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */

class PathEntry(val path: java.awt.geom.GeneralPath, val color: Color) extends Serializable {

  override def equals(other: Any) = other match {
    case that: PathEntry => that.canEqual(this) && this.path == that.path && this.color == that.color
    case _ => false
  }


  override
  def hashCode = 41 * (path.hashCode + 41) + color.hashCode

  override
  def toString = "PathEntry(" + path.toString + "," + color + ")"

  def canEqual(that: PathEntry) = true
}

object PathEntry {
  def apply(path: java.awt.geom.GeneralPath, color: Color) = new PathEntry(path, color)

  // Extractor
  def unapply(pe: PathEntry) = Some((pe.path, pe.color))
}

