package me.chadrs.gtfstools.cli.browse

import com.googlecode.lanterna.gui2.{ActionListBox, BasicWindow, Window, WindowBasedTextGUI}
import me.chadrs.gtfstools.cli.GtfsZipFile
import me.chadrs.gtfstools.csv.CsvRowViewer
import me.chadrs.gtfstools.types.{RoutesFileRow, TripsFileRow}

class MainWindow(gtfsFile: GtfsZipFile) extends BasicWindow("Main Window") with VimArrows {

  implicit def gui: WindowBasedTextGUI = this.getTextGUI

  val actions = new ActionListBox
  actions.addItem("Agencies", fileMenuItem("agency.txt", CsvRowTableView.agenciesTable))
  actions.addItem("Calendars", fileMenuItem("calendar.txt", CsvRowTableView.calendarTable))
  actions.addItem(
    "Calendar Dates",
    fileMenuItem("calendar_dates.txt", CsvRowTableView.calendarDatesTable)
  )
  actions.addItem(
    "Routes",
    fileMenuItem("routes.txt", CsvRowTableView.routesTable, routeDetailWindow)
  )
  actions.addItem("Stops", fileMenuItem("stops.txt", CsvRowTableView.stopsTable))
  actions.addItem("Exit", () => this.close())
  setComponent(actions)

  private def fileMenuItem[RowType: CsvRowViewer](
      file: String,
      tableView: CsvRowTableView[RowType],
      onSelect: Option[RowType => Window] = None
  ): Runnable = { () =>
    {
      gtfsFile
        .parseFile[RowType](file)
        .fold(
          ErrorDialog.show,
          rows => {
            this.getTextGUI
              .addWindowAndWait(new CsvRowTableViewWindow(file, tableView, rows, onSelect))
          }
        )
      ()
    }
  }

  lazy val routeDetailWindow: Option[RoutesFileRow => Window] = Some { route =>
    new RouteDetailWindow(
      route,
      gtfsFile.trips.fold(_ => IndexedSeq.empty, _.filter(r => r.routeId == route.routeId)),
      gtfsFile.stopTimes.getOrElse(IndexedSeq.empty)
    )
  }

}
