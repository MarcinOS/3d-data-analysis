package scribble

import swing._
import swing.event._
import java.awt.Color._

class ScribblePanel extends Panel {
  background = white
  
  //Paths are maintained as a list. When switching to a new color,
  //a new path is added to the list
  //clear clears all paths
  type Path = java.awt.geom.GeneralPath
  var paths: List[(Path, Color)] = (new Path, black) :: Nil

  listenTo(mouse.clicks, mouse.moves, keys)
  reactions += {
    case e: MousePressed => moveTo(e.point)
    case e: MouseDragged => lineTo(e.point)
    case KeyTyped(_, 'c', _, _) => clear
  }

  def lineTo(p: Point) { 
    (paths: @unchecked) match { 
      case (currentPath, _)::_ => currentPath.lineTo(p.x, p.y); repaint()
    }
  }
  def moveTo(p: Point) { 
    (paths: @unchecked) match {
      case (currentPath, _)::_ => currentPath.moveTo(p.x, p.y); repaint() 
    }
  }
  
  def drawPath(g: Graphics2D, p: Path, c: Color) = {
    g.setColor(c)
    g.draw(p)
  }

  override def paintComponent(g: Graphics2D) = {
    super.paintComponent(g)
    for ((p, c) <- paths.reverse) drawPath(g, p, c)
  }
  
  def clear {
    val (_, currentColor) = paths.head
    paths = (new Path, currentColor) :: Nil
    repaint
  }
  
  def setColor(c: java.awt.Color) {
    paths = (new Path, c) :: paths
  }
}

object Scribble extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "Scribble"
    preferredSize = new Dimension(800, 600)
    
    val drawingPanel = new ScribblePanel
    
    contents = new BorderPanel {
      add(new Toolbar(actions = Action("Clear") { drawingPanel.clear }),
        BorderPanel.Position.North)
      add(drawingPanel, BorderPanel.Position.Center)
      add(new ColorSelector(black, red, blue, green, pink, orange, yellow)
      					   (drawingPanel.setColor), 
          BorderPanel.Position.South)
    }
  }
}

//First attempt at a Scala wrapper for JToolBar - doesn't seem to exist in the library
class Toolbar(actions: Action* ) extends Component with Orientable.Wrapper {
  import javax.swing.JToolBar
  override lazy val peer: JToolBar = new JToolBar
  val toolbarButtons = for (a <- actions) yield peer.add(a.peer)
}

class ColorIcon(color: Color) extends Component with javax.swing.Icon {
    // This method draws the icon
    override def paintComponent(g: Graphics2D) {
      g.setColor(color)
      g.drawRect(0, 0, 16, 16)
    }
    def getIconHeight = 16
    def getIconWidth = 16
    
    def paintIcon(c: java.awt.Component, g: java.awt.Graphics, x: Int, y: Int) {
      g.setColor(color);
      g.fillRect(x, y, 16, 16);
    }
  }

// Similar to the Scala Button but is displayed with a uniformly colored rectangle
// instead of a text title
class ColorButton(color: Color)(cb: Color => Unit) extends AbstractButton with Publisher {
  import javax.swing.JButton
  override lazy val peer: JButton = new JButton(new ColorIcon(color)) with SuperMixin
  action = new Action("") {
    def apply() { cb(color) }
    icon = new ColorIcon(color)
  }
}

// "Toolbar" for color selection
class ColorSelector(colors: Color*)(cb: Color => Unit) extends FlowPanel {
  contents ++= colors map ( c => new ColorButton(c)(cb))
//TODO: make one pressed at any time (with buttongroup?)
}

//TODO: toolbar for drawing mode (free drawing, line, circle, fill, etc)

