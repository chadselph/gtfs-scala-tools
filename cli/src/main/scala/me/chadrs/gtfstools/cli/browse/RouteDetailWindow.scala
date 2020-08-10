package me.chadrs.gtfstools.cli.browse

import cats.data.OptionT
import cats.implicits._
import me.chadrs.gtfstools.types.{Routes, RoutesFileRow, StopTimesFileRow, TripsFileRow}

object RouteDetailWindow {

  def nameOf(route: RoutesFileRow): String =
    OptionT(route.routeLongName)
      .orElse(OptionT(route.routeShortName))
      .getOrElseF(route.routeId.map(_.toString))
      .getOrElse("Unnamed Route")

  val tableViewAsMap: CsvRowTableView[RoutesFileRow] =
    CsvRowTableView.fromMapsLike(Routes.Fields.toIndexedSeq, _.toMap)

}

class RouteDetailWindow(
    route: RoutesFileRow,
    trips: IndexedSeq[TripsFileRow],
    stopTimes: IndexedSeq[StopTimesFileRow]
) extends CsvRowDetailViewWindow[RoutesFileRow](
      s"Route ${RouteDetailWindow.nameOf(route)}",
      RouteDetailWindow.tableViewAsMap,
      route
    ) {

  addButton(s"List ${trips.size} Trips", () => popTripsWindow())

  private def popTripsWindow() = {
    val w = new TripsWindow(RouteDetailWindow.nameOf(route), trips, stopTimes)
    this.getTextGUI.addWindowAndWait(w)
  }
}
