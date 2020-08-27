package me.chadrs.gtfstools.cli.subcommands

import caseapp.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsInput
import me.chadrs.gtfstools.cli.GtfsOptions.Rt

object RealtimeCmd {

  def run(opts: Rt, remainingArgs: RemainingArgs): Unit = {
    GtfsInput.fromString(remainingArgs.remaining.head).map(_.toGtfsRtFeed).map { feed =>
      println(feed.toProtoString)
    }
  }
}
