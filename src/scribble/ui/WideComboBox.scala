package scribble.ui

import javax.swing.event.{PopupMenuEvent, PopupMenuListener}
import swing.ComboBox
import java.awt._
import javax.swing.{JComponent, JPopupMenu, JComboBox}

/**
 * Created by IntelliJ IDEA.
 * User: vijayannadi
 * Date: 12/19/12
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */

class WideComboBox[A](items: Seq[A]) extends ComboBox(items) {
  peer.addPopupMenuListener(new PopupMenuListener {
    def popupMenuWillBecomeInvisible(e: PopupMenuEvent) {}

    def popupMenuCanceled(e: PopupMenuEvent) {}

    def popupMenuWillBecomeVisible(e: PopupMenuEvent) {
      val combo = e.getSource match {
        case c: JComboBox[_] => c
        case _ => throw new ClassCastException
      }
      val comp = combo.getUI.getAccessibleChild(combo, 0)
      val popup = comp match {
        case p: JPopupMenu => p
        case _ => throw new ClassCastException
      }
      val scrollPane = popup.getComponent(0) match {
        case jc: JComponent => jc
        case _ => throw new ClassCastException
      }

      val size = new Dimension()
      size.width = 100
      size.height = scrollPane.getPreferredSize.height
      scrollPane.setPreferredSize(size)
      scrollPane.setMaximumSize(size)
    }
  })
}

