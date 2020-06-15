package me.chadrs.gtfstools.cli.browse

import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.gui2.dialogs.{MessageDialog, MessageDialogButton}

object ErrorDialog {
  def show(error: String)(implicit textGUI: WindowBasedTextGUI): MessageDialogButton =
    MessageDialog.showMessageDialog(textGUI, "Error!", error, MessageDialogButton.OK)
}
