package me.chadrs.gtfstools.cli

import caseapp.core.RemainingArgs
import caseapp.core.app.CommandApp
import me.chadrs.gtfstools.cli.GtfsOptions.{
  Browse, DrawShape, FileCommandOptions, HashOptions, Rt, Validate
}
import me.chadrs.gtfstools.cli.subcommands.{
  BrowseCmd, DrawShapeCmd, FileCmd, HashCmd, RealtimeCmd, ValidateCmd
}

object Launcher extends CommandApp[GtfsOptions] {

  override def appName: String = "gtfs"
  override def progName: String = "gtfs"

  def run(options: GtfsOptions, remainingArgs: RemainingArgs): Unit = {
    options match {
      case h: HashOptions => HashCmd.run(h, remainingArgs)
      case fileOptions: FileCommandOptions =>
        FileCmd.run(fileOptions, remainingArgs)
      case b: Browse    => BrowseCmd.run(b, remainingArgs)
      case rt: Rt       => RealtimeCmd.run(rt, remainingArgs)
      case d: DrawShape => DrawShapeCmd.run(d, remainingArgs)
      case v: Validate  => ValidateCmd.run(v, remainingArgs)
      case _            => println("Why not a match error")
    }
  }
}
