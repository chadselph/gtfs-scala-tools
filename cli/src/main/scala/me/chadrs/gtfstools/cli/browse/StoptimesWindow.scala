package me.chadrs.gtfstools.cli.browse

import me.chadrs.gtfstools.types.StopTimesFileRow

class StoptimesWindow(tripName: String, rows: IndexedSeq[StopTimesFileRow])
    extends CsvRowTableViewWindow[StopTimesFileRow](
      s"Stoptimes for $tripName",
      CsvRowTableView.stopTimesTable,
      rows
    ) {}
