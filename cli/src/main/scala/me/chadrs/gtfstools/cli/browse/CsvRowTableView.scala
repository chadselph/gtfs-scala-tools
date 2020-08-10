package me.chadrs.gtfstools.cli.browse

import cats.data.{EitherT, OptionT}
import com.googlecode.lanterna.gui2.table.{Table, TableCellRenderer}
import com.googlecode.lanterna.gui2.{
  BasicWindow, Button, Direction, GridLayout, Label, LinearLayout, Panel, TextGUIGraphics, Window
}
import com.googlecode.lanterna.input.{KeyStroke, KeyType}
import com.googlecode.lanterna.{TerminalSize, TerminalTextUtils, TextColor}
import me.chadrs.gtfstools.types.{
  AgencyFileRow, CalendarDatesFileRow, CalendarFileRow, Color, Routes, RoutesFileRow, StopTimes,
  StopTimesFileRow, Stops, StopsFileRow, Trips, TripsFileRow
}
import cats.implicits._

import scala.util.Try

/**
 * @tparam T the type of CSV row we're displaying in a table.
 */
trait CsvRowTableView[T] {
  def columns: IndexedSeq[CsvRowTableView.Column[T]]
  def renderColumn(obj: T, columnIndex: Int): String = columns(columnIndex).content(obj)
  def customizeRowStyle(graphics: TextGUIGraphics, obj: T, colIndex: Int): Unit = ()
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

  def withColumnsStyled[T](
      doStyle: (TextGUIGraphics, T, Int) => Unit,
      cols: Column[T]*
  ): CsvRowTableView[T] =
    new CsvRowTableView[T] {
      override def columns: IndexedSeq[Column[T]] = cols.toIndexedSeq
      override def customizeRowStyle(graphics: TextGUIGraphics, obj: T, col: Int): Unit =
        doStyle(graphics, obj, col)
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

  def fromMapsLike[T](
      titles: IndexedSeq[String],
      toMap: T => Map[String, String]
  ): CsvRowTableView[T] =
    new CsvRowTableView[T] {
      override def columns: IndexedSeq[Column[T]] =
        titles.map { name =>
          Column(name, toMap(_).getOrElse(name, ""))
        }
    }

  val routesTable: CsvRowTableView[RoutesFileRow] =
    CsvRowTableView.withColumnsStyled[RoutesFileRow](
      (graphics, route, col) => {
        def parseHex(s: String): Option[Int] = Try(Integer.parseInt(s, 16)).toOption
        def colorToLaterna(color: Color): Option[TextColor.RGB] = {
          color.toValue.grouped(2).map(parseHex).toList match {
            case List(Some(r), Some(g), Some(b)) => Option(new TextColor.RGB(r, g, b))
            case _                               => None
          }
        }
        OptionT(route.routeColor)
          .subflatMap(colorToLaterna)
          .map(rgb => graphics.setBackgroundColor(rgb))
        OptionT(route.routeTextColor)
          .subflatMap(colorToLaterna)
          .map(rgb => graphics.setForegroundColor(rgb))
      },
      Column.validated("route_id", _.routeId),
      Column.validatedOpt("route_short_name", _.routeShortName),
      Column.validatedOpt("route_long_name", _.routeLongName)
    )

  val calendarTable: CsvRowTableView[CalendarFileRow] =
    CsvRowTableView.withColumns[CalendarFileRow](
      Column.validated("service_id", _.serviceId),
      Column.validated("start_date", _.startDate),
      Column.validated("end_date", _.endDate),
      Column.apply(
        "days",
        { cal =>
          Seq(
            cal.monday,
            cal.tuesday,
            cal.wednesday,
            cal.thursday,
            cal.friday,
            cal.saturday,
            cal.sunday
          ).zip(Seq("M", "Tu", "W", "Th", "F", "Sa", "Su"))
            .map {
              case (Right(true), d) => d
              case _                => ""
            }
            .mkString
        }
      )
    )
  val calendarDatesTable: CsvRowTableView[CalendarDatesFileRow] =
    CsvRowTableView.withColumns[CalendarDatesFileRow](
      Column.validated("service_id", _.serviceId),
      Column.validated("date", _.date),
      Column.validated(
        "exception_type",
        _.exceptionType.map { exc =>
          exc.toValue match {
            case 1 => "Added"
            case 2 => "Removed"
            case _ => exc.toString
          }
        }
      )
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

  val tripsTableRaw: CsvRowTableView[TripsFileRow] =
    CsvRowTableView.fromMapsLike(Trips.Fields.toIndexedSeq, _.toMap)

  val stopsTable: CsvRowTableView[StopsFileRow] =
    CsvRowTableView.fromMapsLike(Stops.Fields.toIndexedSeq, _.toMap)

  val stopTimesTable: CsvRowTableView[StopTimesFileRow] =
    CsvRowTableView.fromMapsLike(StopTimes.Fields.toIndexedSeq, _.toMap)

}

class CsvRowTableViewWindow[T](
    name: String,
    tableView: CsvRowTableView[T],
    rows: IndexedSeq[T],
    onSelect: Option[T => Window] = None
) extends BasicWindow(name)
    with VimArrows {

  def this(
      name: String,
      tableView: CsvRowTableView[T],
      rows: IndexedSeq[T],
      onSelect: T => Window
  ) = this(name, tableView, rows, Some(onSelect))

  val t = new Table[T](tableView.columns.map(_.title): _*)
  t.setTableCellRenderer(new TableCellRenderer[T] {
    override def getPreferredSize(
        table: Table[T],
        cell: T,
        columnIndex: Int,
        rowIndex: Int
    ): TerminalSize =
      new TerminalSize(
        TerminalTextUtils.getColumnWidth(tableView.renderColumn(cell, columnIndex)),
        1
      )

    override def drawCell(
        table: Table[T],
        cell: T,
        columnIndex: Int,
        rowIndex: Int,
        textGUIGraphics: TextGUIGraphics
    ): Unit = {
      val theme =
        if (t.getSelectedRow == rowIndex) t.getThemeDefinition.getActive
        else t.getThemeDefinition.getNormal
      textGUIGraphics.applyThemeStyle(theme)
      tableView.customizeRowStyle(textGUIGraphics, cell, columnIndex)
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
        onSelect.map(_(rows(t.getSelectedRow))).foreach { window =>
          this.getTextGUI.addWindowAndWait(window)
        }
        true
      case _ => super.handleInput(key)
    }
  }

}

class CsvRowDetailViewWindow[T](name: String, tableView: CsvRowTableView[T], item: T)
    extends BasicWindow(name)
    with VimArrows {
  // re-use existing tableview logic but flip the table
  val mainPanel = new Panel(new LinearLayout(Direction.VERTICAL))
  val detailsPanel = new Panel(new GridLayout(2))
  val buttonsPanel = new Panel(new LinearLayout(Direction.HORIZONTAL))
  mainPanel.addComponent(detailsPanel)
  mainPanel.addComponent(buttonsPanel)
  tableView.columns.foreach { row =>
    detailsPanel.addComponent(new Label(row.title))
    detailsPanel.addComponent(new Label(row.content(item)))
  }

  setComponent(mainPanel)
  setCloseWindowWithEscape(true)

  def addButton(name: String, action: Runnable): Unit = {
    buttonsPanel.addComponent(new Button(name, action))
  }

}
