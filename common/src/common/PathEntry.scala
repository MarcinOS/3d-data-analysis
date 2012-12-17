package common

import java.awt.Color

/**
 * Created by IntelliJ IDEA.
 * User: vijayannadi
 * Date: 12/15/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */

case class PathEntry(path: java.awt.geom.GeneralPath, color: Color) extends Tuple2[java.awt.geom.GeneralPath, Color](path, color)

