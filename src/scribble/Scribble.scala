package scribble

import swing._
import swing.event._
import java.awt.Color._
import akka.kernel.Bootable
import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import com.typesafe.config.ConfigFactory
import javax.swing.SwingUtilities
import common._
import common.ScribbleStroke._
import ui.{WideComboBox, MigPanel}
import java.awt.{Color, BasicStroke}
import swing.Color
import swing.Graphics2D

class ScribblePanel extends Panel {
  background = white

  var currentColor: Color = Color.black
  var currentStroke: ScribbleStroke = DEFAULT_STROKE

  def init() {
    currentColor = Color.black
    currentStroke = DEFAULT_STROKE
  }

  //Paths are maintained as a list. When switching to a new color,
  //a new path is added to the list
  //clear clears all paths
  type Path = java.awt.geom.GeneralPath
  var paths: List[PathEntry] = PathEntry(new Path, black) :: Nil

  listenTo(mouse.clicks, mouse.moves, keys)
  reactions += {
    case e: MousePressed => moveTo(e.point)
    case e: MouseDragged => lineTo(e.point)
    case e: MouseReleased => sendPathToServer()
    case KeyTyped(_, 'c', _, _) => clear()
  }

  def sendPathToServer() {
    (paths: @unchecked) match {
      case pathEntry :: _ => Scribble.sComm.sendPathToServer(pathEntry)
    }
  }

  def lineTo(p: Point) {
    (paths: @unchecked) match {
      case pathEntry :: _ => pathEntry.path.lineTo(p.x, p.y); repaint()
    }
  }

  def moveTo(p: Point) {
    (paths: @unchecked) match {
      case pathEntry :: _ => pathEntry.path.moveTo(p.x, p.y); repaint()
    }
  }

  def drawPath(g: Graphics2D, p: PathEntry) {
    g.setStroke(p.stroke)
    g.setColor(p.color)
    g.draw(p.path)
  }

  def addPath(path: PathEntry) {
    //val currentColor = paths.head.color
    paths ::= path
    paths = PathEntry(new Path, currentColor, currentStroke) :: paths
    SwingUtilities.invokeLater(new Runnable {
      def run() {
        peer.repaint()
      }
    })
  }

  def setPaths(newPaths: List[PathEntry]) {
    // TODO synchronization of changes
    init()

    if (newPaths.isEmpty) {
      paths = PathEntry(new Path, currentColor, currentStroke) :: Nil
    } else {
      paths = PathEntry(new Path, currentColor, currentStroke) :: newPaths
    }
    paths = PathEntry(new Path, currentColor, currentStroke) :: paths
    SwingUtilities.invokeLater(new Runnable {
      def run() {
        peer.repaint()
      }
    })
  }

  override def paintComponent(g: Graphics2D) {
    super.paintComponent(g)
    for (path <- paths.reverse) drawPath(g, path)
  }

  def clear() {
    //val currentColor = paths.head.color
    paths = PathEntry(new Path, currentColor, currentStroke) :: Nil

    SwingUtilities.invokeLater(new Runnable {
      def run() {
        peer.repaint()
      }
    })

    Scribble.sComm.clear()
  }

  def setColor(c: java.awt.Color) {
    currentColor = c
    paths = PathEntry(new Path, c, currentStroke) :: paths
  }

  def setStroke(stroke: BasicStroke) {
    //val currentColor = paths.head.color
    currentStroke = stroke
    paths = PathEntry(new Path, currentColor, stroke) :: paths
  }
}

class ScribbleCommunicator extends Bootable {
  //#setup
  val system = ActorSystem("Scribble", ConfigFactory.load.getConfig("scribble"))
  val actor = system.actorOf(Props[ScribbleActor], "ScribbleActor")
  val remoteActor = system.actorFor("akka://ServerApplication@127.0.0.1:2552/user/server")

  def register(name: String) {
    if (actor.isTerminated)
      actor !(remoteActor, Register(name))
  }

  def sendPathToServer(path: PathEntry) {
    actor !(remoteActor, AddPath(path))
  }

  def clear() {
    actor !(remoteActor, Clear)
  }

  def startup() {
  }

  def shutdown() {
    actor !(remoteActor, Bye)
    system.shutdown()
  }
}

class ScribbleActor extends Actor {
  def receive = {
    case (actor: ActorRef, op: Operation) ⇒ actor ! op
    case pathUpdate: PathEntry ⇒ Scribble.top.addPath(pathUpdate)
    case Hello(initialPaths) => Scribble.top.setPaths(initialPaths)
  }
}

class ScribbleFrame extends MainFrame {

  import common.ScribbleStroke._

  title = "Scribble"
  preferredSize = new Dimension(800, 600)

  val drawingPanel = new ScribblePanel

  contents = new BorderPanel {
    add(new Toolbar(actions = Action("Clear") {
      drawingPanel.clear()
    }), BorderPanel.Position.North)
    add(drawingPanel, BorderPanel.Position.Center)
    add(new MigPanel("wrap 2", "center", "center") {
      contents += new ColorSelector(black, red, blue, green, pink, orange, yellow)(drawingPanel.setColor)
      contents +=(new StrokeSelector(getPossibleStrokes)(drawingPanel.setStroke), "width 10:80:80")
    }, BorderPanel.Position.South
    )
  }

  def getPossibleStrokes: Seq[BasicStroke] = {
    List(
      DEFAULT_STROKE,
      new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f),
      new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, Array(5.0f, 5.0f), 0.0f),
      new BasicStroke(5.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, Array(5.0f, 5.0f), 0.0f)
    )
  }

  def addPath(path: PathEntry) {
    println("Adding path " + path)
    drawingPanel.addPath(path)
  }

  def setPaths(paths: List[PathEntry]) {
    println("Adding paths " + paths)
    drawingPanel.setPaths(paths)
  }
}

object Scribble extends SimpleSwingApplication {
  val sComm: ScribbleCommunicator = new ScribbleCommunicator
  val topFrame = new ScribbleFrame

  def top = topFrame

  override def startup(args: Array[String]) {
    super.startup(args)
    sComm.register("test")
  }
}

//First attempt at a Scala wrapper for JToolBar - doesn't seem to exist in the library
class Toolbar(actions: Action*) extends Component with Orientable.Wrapper {

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
    g.setColor(color)
    g.fillRect(x, y, 16, 16)
  }
}

// Similar to the Scala Button but is displayed with a uniformly colored rectangle
// instead of a text title
class ColorButton(color: Color)(cb: Color => Unit) extends AbstractButton with Publisher {

  import javax.swing.JButton

  override lazy val peer: JButton = new JButton(new ColorIcon(color)) with SuperMixin
  action = new Action("") {
    def apply() {
      cb(color)
    }

    icon = new ColorIcon(color)
  }
}

class StrokeCombo(strokes: Seq[BasicStroke])(ss: BasicStroke => Unit) extends WideComboBox[BasicStroke](strokes) with Publisher {
  listenTo(selection)
  val dhis = this
  reactions += {
    case SelectionChanged(`dhis`) => {
      this.selection.item match {
        case bs: BasicStroke => ss(ScribbleStroke(bs))
        case _ => println("Non basic stroke")
      }
    }
  }

  renderer = new ListView.Renderer[BasicStroke] {
    def componentFor(list: ListView[_], isSelected: Boolean, hasFocus: Boolean, s: BasicStroke, index: Int): Component = {
      new scala.swing.Panel {
        peer.setPreferredSize(new Dimension(100, 20))
        peer.setOpaque(true)

        override def paintComponent(g: Graphics2D) {
          super.paintComponent(g)
          var strokeColor = Color.BLACK
          val w = list.peer.getPreferredSize.getWidth.toInt
          val h = peer.getPreferredSize.getHeight.toInt
          if (isSelected) {
            g.setColor(list.peer.getSelectionBackground)
            strokeColor = Color.WHITE
            g.fillRect(0, 0, w, h)
          }

          g.setColor(strokeColor)
          g.setStroke(s)
          g.drawLine(0, h / 2, w, h / 2)
        }
      }
    }
  }
}

class StrokeSelector(strokes: Seq[BasicStroke])(ss: BasicStroke => Unit) extends BorderPanel {
  add(new StrokeCombo(strokes)(ss), BorderPanel.Position.Center)
}

// "Toolbar" for color selection
class ColorSelector(colors: Color*)(cb: Color => Unit) extends FlowPanel {
  contents ++= colors map (c => new ColorButton(c)(cb))
  //TODO: make one pressed at any time (with buttongroup?)
}

//TODO: toolbar for drawing mode (free drawing, line, circle, fill, etc)

