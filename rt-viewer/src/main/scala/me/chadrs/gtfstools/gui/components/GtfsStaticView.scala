package me.chadrs.gtfstools.gui.components

import me.chadrs.gtfstools.parsing.GtfsZipFile
import me.chadrs.gtfstools.types.{
  Agency, RoutesFileRow, CalendarFileRow, Routes, AgencyFileRow, Calendar
}
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ScrollPane, Label, TableColumn, TableView}

object GtfsStaticView {

  def apply[A: StaticViewColumns](
      gtfs: GtfsZipFile,
      getRecords: GtfsZipFile => Either[String, IndexedSeq[A]]
  ): ScrollPane = {
    val recordsOrError = getRecords.apply(gtfs).map(as => ObservableBuffer[A](as: _*))
    new ScrollPane() {
      content = recordsOrError.fold(
        err => new Label(err),
        records => implicitly[StaticViewColumns[A]].toView(records)
      )
    }
  }
}

trait StaticViewColumns[A] {
  def toMap(a: A): Map[String, String]
  def columnOrder: Seq[String]

  def toView(a: ObservableBuffer[A]): TableView[A] = {
    new TableView[A](a) {
      columns ++= columnOrder.map(colName => {
        new TableColumn[A, String] {
          text = colName
          cellValueFactory = (a) => new StringProperty(toMap(a.value).getOrElse(colName, ""))
        }
      })
    }
  }
}

object StaticViewColumns {
  implicit val agenciesColumn: StaticViewColumns[AgencyFileRow] =
    new StaticViewColumns[AgencyFileRow] {
      override def toMap(agency: AgencyFileRow): Map[String, String] = agency.toMap
      override def columnOrder: Seq[String] = Agency.Fields
    }

  implicit val routesSvc: StaticViewColumns[RoutesFileRow] =
    new StaticViewColumns[RoutesFileRow] {
      override def toMap(route: RoutesFileRow): Map[String, String] = route.toMap
      override def columnOrder: Seq[String] = Routes.Fields
    }

  implicit val calsSvc: StaticViewColumns[CalendarFileRow] =
    new StaticViewColumns[CalendarFileRow] {
      override def toMap(cal: CalendarFileRow): Map[String, String] = cal.toMap
      override def columnOrder: Seq[String] = Calendar.Fields
    }
}
