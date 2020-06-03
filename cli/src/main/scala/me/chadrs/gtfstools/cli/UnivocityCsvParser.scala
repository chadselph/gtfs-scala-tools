package me.chadrs.gtfstools.cli

import java.io.ByteArrayInputStream

import com.univocity.parsers.csv._
import me.chadrs.gtfstools.csv.CsvFile

import scala.collection.immutable.ArraySeq
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

}
