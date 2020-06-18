package me.chadrs.gtfstools.cli.browse

import cats.data.OptionT
import cats.implicits._
import com.googlecode.lanterna.gui2.{ActionListBox, BasicWindow, WindowBasedTextGUI}
import me.chadrs.gtfstools.cli.GtfsZipFile
import me.chadrs.gtfstools.csv.CsvRowViewer
import me.chadrs.gtfstools.types.{RouteId, Routes, RoutesFileRow, Trips, TripsFileRow}

class MainWindow(gtfsFile: GtfsZipFile) extends BasicWindow("Main Window") with VimArrows {

  implicit def gui: WindowBasedTextGUI = this.getTextGUI

  val actions = new ActionListBox
  actions.addItem("Routes", fileMenuItem("routes.txt", CsvRowTableView.routesTable, routeDetailWindow))
  actions.addItem("Calendars", fileMenuItem("calendar.txt", CsvRowTableView.calendarTable))
  actions.addItem("Calendar Dates", fileMenuItem("calendar_dates.txt", CsvRowTableView.calendarDatesTable))
  actions.addItem("Agencies", fileMenuItem("agency.txt", CsvRowTableView.agenciesTable))
  actions.addItem("Exit", () => this.close())
  setComponent(actions)

  private def fileMenuItem[RowType: CsvRowViewer](
      file: String,
      tableView: CsvRowTableView[RowType],
      onSelect: RowType => () = (_: RowType) => ()
  ): Runnable = { () =>
    {
      gtfsFile
        .parseFile[RowType](file)
        .fold(
          ErrorDialog.show,
          rows => popCsvRowTableWindow(file, tableView, rows, onSelect)
        )
      ()
    }
  }

  private def popCsvRowTableWindow[T](title: String, tableView: CsvRowTableView[T], rows: IndexedSeq[T], onSelect: T => Unit = (_: T) => ()): Unit = {
    this.getTextGUI
      .addWindowAndWait(new CsvRowTableViewWindow(title, tableView, rows, onSelect))

  }

  def routeDetailWindow(route: RoutesFileRow): Unit = {
    val name = OptionT(route.routeLongName)
      .orElse(OptionT(route.routeShortName))
      .getOrElseF(route.routeId.map(_.toString))
      .getOrElse("Unnamed Route")
    val buttons: RoutesFileRow => Seq[(String, Runnable)] = { r: RoutesFileRow =>
      val matchingTrips = gtfsFile.trips.getOrElse(Nil).filter(_.routeId == r.routeId).toIndexedSeq
      Seq(
        (s"List ${matchingTrips.size} Trips", () => popCsvRowTableWindow(s"$name trips", CsvRowTableView.tripsTableRaw, matchingTrips))
      )
    }
    val w = new CsvRowDetailViewWindow[RoutesFileRow](
      name, CsvRowTableView.fromMapsLike(Routes.Fields.toIndexedSeq, _.toMap), route, buttons)
    this.getTextGUI.addWindowAndWait(w)
  }

}
