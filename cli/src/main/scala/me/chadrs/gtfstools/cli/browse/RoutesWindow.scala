package me.chadrs.gtfstools.cli.browse

import com.googlecode.lanterna.gui2.table.{Table, TableCellRenderer}
import com.googlecode.lanterna.gui2.{BasicWindow, TextGUIGraphics}
import com.googlecode.lanterna.{TerminalSize, TerminalTextUtils}
import me.chadrs.gtfstools.types.{Routes, RoutesFileRow}

class RoutesWindow(routes: Seq[RoutesFileRow]) extends BasicWindow("Routes") {
  val t = new Table[RoutesFileRow](Routes.Fields: _*)
  t.setTableCellRenderer(new TableCellRenderer[RoutesFileRow] {
    override def getPreferredSize(
        table: Table[RoutesFileRow],
        cell: RoutesFileRow,
        columnIndex: Int,
        rowIndex: Int
    ): TerminalSize =
      new TerminalSize(TerminalTextUtils.getColumnWidth(getContent(cell, columnIndex)), 1)

    override def drawCell(
        table: Table[RoutesFileRow],
        cell: RoutesFileRow,
        columnIndex: Int,
        rowIndex: Int,
        textGUIGraphics: TextGUIGraphics
    ): Unit = textGUIGraphics.putString(0, 0, getContent(cell, columnIndex))

    def getContent(cell: RoutesFileRow, colIndex: Int): String =
      cell.get(Routes.Fields(colIndex)).getOrElse("")
  })
  ErrorDialog.show(this.getSize.getRows.toString)(this.getTextGUI)
  t.setVisibleRows(40)
  routes.foreach(r => t.getTableModel.addRow(Seq.fill(Routes.Fields.size)(r): _*))
  setComponent(t)
  this.setCloseWindowWithEscape(true)
}
