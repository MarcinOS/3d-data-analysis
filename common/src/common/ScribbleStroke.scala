package common

import java.awt.BasicStroke

/**
 * Created with IntelliJ IDEA.
 * User: vijayannadi
 * Date: 12/20/12
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
class ScribbleStroke(val width: Float, val cap: Int, val join: Int, val miterlimit: Float, val dash: Array[Float], val dashPhase: Float) extends Serializable {

}

object ScribbleStroke {
  val DEFAULT_STROKE: ScribbleStroke = ScribbleStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f))

  implicit def stroke2ScribbleStroke(bs: BasicStroke): ScribbleStroke = ScribbleStroke(bs)

  implicit def scribbleStroke2Stroke(ss: ScribbleStroke): BasicStroke = new BasicStroke(ss.width, ss.cap, ss.join, ss.miterlimit, ss.dash, ss.dashPhase)

  def apply(s: java.awt.BasicStroke) = new ScribbleStroke(s.getLineWidth, s.getEndCap, s.getLineJoin, s.getMiterLimit, s.getDashArray, s.getDashPhase)
}