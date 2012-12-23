package scribble.ui

import swing.{Component, LayoutContainer, Panel}
import net.miginfocom.swing.MigLayout

class MigPanel(constraints: String, colConst: String, rowConst: String) extends Panel with LayoutContainer {

  def this(constraints: String) = this(constraints, "", "")

  type Constraints = String

  def layoutManager = peer.getLayout.asInstanceOf[MigLayout]

  override lazy val peer = new javax.swing.JPanel(new MigLayout(constraints, colConst, rowConst)) with SuperMixin

  override def contents: MigContent = new MigContent

  protected class MigContent extends Content {
    def +=(c: Component, l: Constraints) {
      add(c, l)
    }
  }

  protected def constraintsFor(comp: Component) =
    layoutManager.getConstraintMap.get(comp.peer).toString

  protected def areValid(c: Constraints): (Boolean, String) = (true, "")

  protected def add(c: Component, l: Constraints) {
    peer.add(c.peer, l)
  }

}