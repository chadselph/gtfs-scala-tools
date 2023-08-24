package me.chadrs.gtfstools.csv

import java.io.{ByteArrayInputStream, InputStream}

import org.parboiled2._

sealed trait IsRequired
case object Required extends IsRequired
case object ConditionallyRequired extends IsRequired
case object Optional extends IsRequired

case class CsvFile(headers: Seq[String], rows: Seq[Seq[String]])

class CsvParser(val input: ParserInput) extends Parser {

  val DQUOTE: Char = '\u0022'

  def NL: Rule0 = rule(optional('\r') ~ '\n')

  def csvFile: Rule1[CsvFile] = rule {
    (csvRow ~ NL ~ zeroOrMore(csvRow).separatedBy(NL)) ~> CsvFile
  }

  def csvRow: Rule1[Seq[String]] = rule {
    oneOrMore(csvValue).separatedBy(",")
  }

  def csvValue: Rule1[String] = rule {
    csvQuoted | csvNotQuoted
  }
  def csvQuoted: Rule1[String] = rule {
    (DQUOTE ~ zeroOrMore(quotedData | escapeQuote) ~ DQUOTE) ~> ((x: Seq[String]) => x.mkString)
  }

  def csvNotQuoted: Rule1[String] = rule {
    capture(zeroOrMore(noneOf("\",\r\n")))
  }

  def quotedData: Rule1[String] = rule {
    capture(noneOf(DQUOTE.toString))
  }

  def escapeQuote: Rule1[String] = rule(("\"\"" | "\\\"") ~ push(DQUOTE.toString))

}

object CsvParser {
  import org.parboiled2.Parser.DeliveryScheme.Either
  def parseFile(bytes: Array[Byte]) = {
    val parser = new CsvParser(ParserInput(new String(bytes)))
    parser.csvFile.run()
  }
}
