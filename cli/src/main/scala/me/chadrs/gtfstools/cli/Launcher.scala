package me.chadrs.gtfstools.cli

import caseapp.core.RemainingArgs
import caseapp.core.app.CommandApp
import me.chadrs.gtfstools.cli.GtfsOptions.{Browse, FileCommandOptions, HashOptions, Rt}
import me.chadrs.gtfstools.cli.subcommands.{BrowseCmd, FileCmd, HashCmd, RealtimeCmd}

object Launcher extends CommandApp[GtfsOptions] {

  override def appName: String = "gtfs"
  override def progName: String = "gtfs"

  def run(options: GtfsOptions, remainingArgs: RemainingArgs): Unit = {
    options match {
      case h: HashOptions => HashCmd.run(h, remainingArgs)
      case fileOptions: FileCommandOptions =>
        FileCmd.run(fileOptions, remainingArgs)
      case b: Browse => BrowseCmd.run(b, remainingArgs)
      case rt: Rt    => RealtimeCmd.run(rt, remainingArgs)
      case _         => println("Why not a match error")
    }
  }
}
