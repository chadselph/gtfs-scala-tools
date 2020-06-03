package me.chadrs.gtfstools.bench

import org.parboiled2.{CharPredicate, Parser, ParserInput, Rule, Rule0, Rule1}
import shapeless.HNil

// From https://maciejbdotme.wordpress.com/2014/07/11/a-csv-parser-moving-from-scala-parser-combinators-to-parboiled2/

case class Parboiled2CsvParser(input: ParserInput, delimeter: String) extends Parser {

  def DQUOTE = '"'
  def DELIMITER_TOKEN = rule(capture(delimeter))
  def DQUOTE2 = rule("\"\"" ~ push("\""))
  def CRLF = rule(capture("\n\r" | "\n"))
  def NON_CAPTURING_CRLF = rule("\n\r" | "\n")

  val delims = s"$delimeter\r\n" + DQUOTE
  def TXT = rule(capture(!anyOf(delims) ~ ANY))
  val WHITESPACE = CharPredicate(" \t")
  def SPACES: Rule0 = rule(oneOrMore(WHITESPACE))

  def escaped = rule(optional(SPACES) ~
    DQUOTE ~ (zeroOrMore(DELIMITER_TOKEN | TXT | CRLF | DQUOTE2) ~ DQUOTE ~
    optional(SPACES)) ~> (_.mkString("")))
  def nonEscaped = rule(zeroOrMore(TXT | capture(DQUOTE)) ~> (_.mkString("")))

  def field = rule(escaped | nonEscaped)

  def row: Rule1[Seq[String]] = rule(oneOrMore(field).separatedBy(delimeter))

  def file: Rule1[Seq[Seq[String]]] = rule(zeroOrMore(row).separatedBy(NON_CAPTURING_CRLF))
}
