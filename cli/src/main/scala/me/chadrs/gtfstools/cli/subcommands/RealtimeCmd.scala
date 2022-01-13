package me.chadrs.gtfstools.cli.subcommands

import caseapp.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.Rt
import me.chadrs.gtfstools.parsing.GtfsInput

object RealtimeCmd {

  def run(opts: Rt, remainingArgs: RemainingArgs): Unit = {
    GtfsInput.fromString(remainingArgs.remaining.head).map(_.toGtfsRtFeed).map { feed =>
      println(feed.toProtoString)
    }
  }
}
