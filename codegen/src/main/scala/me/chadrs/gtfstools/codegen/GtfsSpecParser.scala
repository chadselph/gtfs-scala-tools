package me.chadrs.gtfstools.codegen

import me.chadrs.gtfstools.csv.CsvParser
import org.parboiled2._

sealed trait IsRequired
case object Required extends IsRequired
case object ConditionallyRequired extends IsRequired
case object Optional extends IsRequired

case class GtfsFieldSpec(name: String, `type`: String, required: IsRequired, description: String)

case class GtfsFileSpec(filename: String, required: IsRequired, description: String, fields: Seq[GtfsFieldSpec])

class GtfsSpecParser(input: ParserInput) extends CsvParser(input) {


  def spec : Rule1[Seq[GtfsFileSpec]] = rule {
    oneOrMore(gtfsFile).separatedBy(NL) ~ EOI
  }

  def gtfsFile: Rule1[GtfsFileSpec] = rule {
    (textFileLine ~ NL ~ emptyLine ~ NL ~ fileRequiredLine ~ NL ~ 
      docLines ~ NL ~ fileFieldsHeaderRow ~ NL ~ oneOrMore(gtfsField).separatedBy(NL)) ~> GtfsFileSpec
  }
  
  def gtfsField: Rule1[GtfsFieldSpec] = rule {
    (csvValue ~ "," ~ csvValue ~ "," ~ isRequired ~ "," ~ csvValue) ~> GtfsFieldSpec
  }
  
  def fileRequiredLine: Rule1[IsRequired] = rule {
    "File: " ~ isRequired ~ ",,,"
  }

  def emptyLine: Rule0 = rule(",,,")
  
  def docLine: Rule1[String] = rule {
    csvValue ~ ",,,"
  }

  def textFileLine = rule {
    capture(textFileName) ~ ",,,"
  }
  def docLines: Rule1[String] = rule {
    zeroOrMore(docLine).separatedBy(NL) ~> { docs: Seq[String] => docs.mkString("\n") }
  }

  def isRequired: Rule1[IsRequired] = rule {
    "Required" ~ push(Required) |
      "Optional" ~ push(Optional) |
      "Conditionally " ~ ("R" | "r") ~ "equired" ~ push(ConditionallyRequired)
  }

  def fileFieldsHeaderRow: Rule0 = rule {
    "Field Name,Type,Required,Description"
  }
  
  val validFileNameChars: CharPredicate = CharPredicate.Alpha ++ "_"

  def textFileName: Rule0 = rule {
    oneOrMore(validFileNameChars) ~ ".txt"
  }

}

object GtfsSpecParser {

  import org.parboiled2.Parser.DeliveryScheme.Either
  def runParser(s: String) = {
    val parser = new GtfsSpecParser(ParserInput(s))
    val result = parser.spec.run()
    result.left.map(parser.formatError(_))
  }

  def main(args: Array[String]): Unit = {
    runParser(Spec.text).getOrElse(Nil).foreach(println)
  }

}