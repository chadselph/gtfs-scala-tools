package me.chadrs.gtfstools.parsing

import java.io.ByteArrayInputStream
import scala.collection.immutable.ArraySeq
import com.univocity.parsers.csv.{CsvParserSettings, CsvWriter, CsvWriterSettings, CsvParser}
import me.chadrs.gtfstools.csv.CsvFile
import scala.jdk.CollectionConverters._

object UnivocityCsvParser {
  private val parser = {
    val settings = new CsvParserSettings()
    settings.setEmptyValue("")
    settings.setNullValue("")
    new CsvParser(settings)
  }

  def parseFile(bytes: Array[Byte]): CsvFile = {
    parser.beginParsing(new ByteArrayInputStream(bytes))
    val headers = ArraySeq.unsafeWrapArray(parser.parseNext())
    val rows = parser.parseAll()
    CsvFile(headers, rows.asScala.toIndexedSeq.map(ArraySeq.unsafeWrapArray))
  }

  def writeCsv(data: Array[Array[String]]): Iterable[String] = {
    val w = new CsvWriter(new CsvWriterSettings())
    w.writeRowsToString(data.asInstanceOf[Array[Array[AnyRef]]]).asScala
  }

}
