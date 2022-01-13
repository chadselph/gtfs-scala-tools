package me.chadrs.gtfstools.cli.subcommands

import caseapp.CaseApp
import caseapp.core.RemainingArgs
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import me.chadrs.gtfstools.cli.GtfsOptions.Browse
import me.chadrs.gtfstools.cli.browse.MainWindow
import me.chadrs.gtfstools.parsing.GtfsInput

object BrowseCmd extends CaseApp[Browse] {
  override def run(options: Browse, remainingArgs: RemainingArgs): Unit = {
    GtfsInput.fromString(remainingArgs.remaining.head).map(_.toGtfsZipFile).map { feed =>
      val term = new DefaultTerminalFactory().createTerminal()
      val screen = new TerminalScreen(term)
      val gui = new MultiWindowTextGUI(screen)
      screen.startScreen()
      val mainWindow = new MainWindow(feed)
      gui.addWindow(mainWindow)
      mainWindow.waitUntilClosed();
      screen.stopScreen()
    }
  }
}
