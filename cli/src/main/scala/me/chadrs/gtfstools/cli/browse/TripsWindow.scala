package me.chadrs.gtfstools.cli.browse

import me.chadrs.gtfstools.types.{StopTimesFileRow, TripsFileRow}

object TripsWindow {}

class TripsWindow(
    routeName: String,
    rows: IndexedSeq[TripsFileRow],
    stopTimes: IndexedSeq[StopTimesFileRow]
) extends CsvRowTableViewWindow[TripsFileRow](
      s"$routeName Trips",
      CsvRowTableView.tripsTableRaw,
      rows,
      selected =>
        new StoptimesWindow(
          selected.tripId.map(_.toString).getOrElse("Unknown trip"),
          stopTimes.filter(_.tripId == selected.tripId)
        )
    )
