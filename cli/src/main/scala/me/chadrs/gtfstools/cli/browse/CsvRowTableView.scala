package me.chadrs.gtfstools.cli.browse

import com.googlecode.lanterna.gui2.table.{Table, TableCellRenderer}
import com.googlecode.lanterna.gui2.{BasicWindow, Button, Direction, GridLayout, Label, LinearLayout, Panel, TextGUIGraphics}
import com.googlecode.lanterna.input.{KeyStroke, KeyType}
import com.googlecode.lanterna.{TerminalSize, TerminalTextUtils}
import me.chadrs.gtfstools.types.{AgencyFileRow, CalendarDatesFileRow, CalendarFileRow, Routes, RoutesFileRow, Trips, TripsFileRow}

/**
 * @tparam T the type of CSV row we're displaying in a table.
 */
trait CsvRowTableView[T] {
  def columns: IndexedSeq[CsvRowTableView.Column[T]]
  def renderColumn(obj: T, columnIndex: Int): String = columns(columnIndex).content(obj)
}

object CsvRowTableView {
  case class Column[T](title: String, content: T => String)

  object Column {
    def validatedOpt[T](name: String, data: T => Either[String, Option[_]]): Column[T] = {
      validated(name, data.andThen(_.map(_.getOrElse(""))))
    }
    def validated[T](name: String, data: T => Either[String, _]): Column[T] = {
      Column(
        name,
        data.andThen {
          case Left(err) =>
            // color the column red?
            s"(${err})"
          case Right(other) => other.toString
        }
      )
    }
  }

  def withColumns[T](cols: Column[T]*): CsvRowTableView[T] =
    new CsvRowTableView[T] {
      override def columns: IndexedSeq[Column[T]] = cols.toIndexedSeq
    }

  def fromMaps(titles: IndexedSeq[String]): CsvRowTableView[Map[String, String]] =
    new CsvRowTableView[Map[String, String]] {
      override def columns: IndexedSeq[Column[Map[String, String]]] =
        titles.map { name =>
          Column(name, _.getOrElse(name, ""))
        }
    }

  def fromMapsLike[T](titles: IndexedSeq[String], toMap: T => Map[String, String]): CsvRowTableView[T] =
    new CsvRowTableView[T] {
      override def columns: IndexedSeq[Column[T]] =
        titles.map { name =>
          Column(name, toMap(_).getOrElse(name, ""))
        }
    }

  val routesTable: CsvRowTableView[RoutesFileRow] = CsvRowTableView.withColumns[RoutesFileRow](
    Column.validated("route_id", _.routeId),
    Column.validatedOpt("route_short_name", _.routeShortName),
    Column.validatedOpt("route_long_name", _.routeLongName)
  )

  val calendarTable: CsvRowTableView[CalendarFileRow] = CsvRowTableView.withColumns[CalendarFileRow](
    Column.validated("service_id", _.serviceId),
    Column.validated("start_date", _.startDate),
    Column.validated("end_date", _.endDate),
    Column.apply("days", { cal =>
      Seq(cal.monday, cal.tuesday, cal.wednesday, cal.thursday, cal.friday, cal.saturday, cal.sunday)
        .zip(Seq("M", "Tu", "W", "Th", "F", "Sa", "Su"))
        .map {
          case (Right(true), d) => d
          case _ => ""
        }
        .mkString
    })

  )
  val calendarDatesTable: CsvRowTableView[CalendarDatesFileRow] = CsvRowTableView.withColumns[CalendarDatesFileRow](
    Column.validated("service_id", _.serviceId),
    Column.validated("date", _.date),
    Column.validated("exception_type", _.exceptionType.map { exc =>
      exc.toValue match {
        case 1 => "Added"
        case 2 => "Removed"
        case _ => exc.toString
      }
    })
  )

  val agenciesTable: CsvRowTableView[AgencyFileRow] = CsvRowTableView.withColumns(
    Column.validatedOpt("agency_id", _.agencyId),
    Column.validated("agency_name", _.agencyName),
    Column.validated("agency_url", _.agencyUrl),
    Column.validated("agency_timezone", _.agencyTimezone),
    Column.validatedOpt("agency_lang", _.agencyLang),
    Column.validatedOpt("agency_phone", _.agencyPhone),
    Column.validatedOpt("agency_fare_url", _.agencyFareUrl),
    Column.validatedOpt("agency_email", _.agencyEmail)
  )

  val tripsTableRaw: CsvRowTableView[TripsFileRow] = CsvRowTableView.fromMapsLike(Trips.Fields.toIndexedSeq, _.toMap)

}

class CsvRowTableViewWindow[T](name: String, tableView: CsvRowTableView[T], rows: IndexedSeq[T], onSelect: T => ()) extends BasicWindow(name) with VimArrows {
  val t = new Table[T](tableView.columns.map(_.title): _*)
  t.setTableCellRenderer(new TableCellRenderer[T] {
    override def getPreferredSize(
       table: Table[T],
       cell: T,
       columnIndex: Int,
       rowIndex: Int
     ): TerminalSize =
      new TerminalSize(TerminalTextUtils.getColumnWidth(
        tableView.renderColumn(cell, columnIndex)
      ), 1)

    override def drawCell(
     table: Table[T],
     cell: T,
     columnIndex: Int,
     rowIndex: Int,
     textGUIGraphics: TextGUIGraphics
   ): Unit = {
      val theme = if (t.getSelectedRow == rowIndex) t.getThemeDefinition.getActive else t.getThemeDefinition.getNormal
      textGUIGraphics.applyThemeStyle(theme)
      textGUIGraphics.putString(0, 0, tableView.renderColumn(cell, columnIndex))
    }

  })
  rows.foreach(r => t.getTableModel.addRow(Seq.fill(tableView.columns.size)(r): _*))
  setComponent(t)
  this.setCloseWindowWithEscape(true)
  t.setVisibleRows(25)

  def setRowsVisible(rows: Int): Unit = t.setVisibleRows(rows)

  override def handleInput(key: KeyStroke): Boolean = {
    key match {
      case LanternaScala.KeyStroke(KeyType.Enter, _) =>
        onSelect.apply(rows(t.getSelectedRow))
        true
      case _ => super.handleInput(key)
    }
  }

}

class CsvRowDetailViewWindow[T](name: String, tableView: CsvRowTableView[T], item: T, buttons: Seq[(String, T => ())])
  extends BasicWindow(name) with VimArrows {
  // re-use existing tableview logic but flip the table
  val t = new Table[String]("", "")
  val mainPanel = new Panel(new LinearLayout(Direction.VERTICAL))
  val detailsPanel = new Panel(new GridLayout(2))
  val buttonsPanel = new Panel(new LinearLayout(Direction.HORIZONTAL))
  mainPanel.addComponent(detailsPanel)
  mainPanel.addComponent(buttonsPanel)
  tableView.columns.foreach { row =>
    detailsPanel.addComponent(new Label(row.title))
    detailsPanel.addComponent(new Label(row.content(item)))
  }

  buttons.foreach { b =>
    buttonsPanel.addComponent(new Button(b._1, () => b._2(item)))
  }
  setComponent(mainPanel)
  setCloseWindowWithEscape(true)

}

