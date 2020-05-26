package me.chadrs.gtfstools.cli

import caseapp.core.RemainingArgs
import caseapp.core.app.CommandApp
import me.chadrs.gtfstools.cli.GtfsOptions.{FileCommandOptions, HashOptions}
import me.chadrs.gtfstools.cli.subcommands.{FileCmd, HashCmd}

object Launcher extends CommandApp[GtfsOptions] {

  override def appName: String = "gtfs"
  override def progName: String = "gtfs"

  def run(options: GtfsOptions, remainingArgs: RemainingArgs): Unit = {
    options match {
      case h: HashOptions => HashCmd.run(h, remainingArgs)
      case fileOptions: FileCommandOptions =>
        FileCmd.run(fileOptions, remainingArgs)
    }
  }
}
