package me.chadrs.gtfstools.cli

import caseapp.core.argparser.{SimpleArgParser, ArgParser}
import caseapp.core.Error

import java.time.LocalDate
import scala.util.Try

trait ArgParsers {
  implicit val dateArgParser: ArgParser[LocalDate] =
    SimpleArgParser.from[LocalDate]("custom") { s =>
      Try(LocalDate.parse(s)).toEither.left.map(ex => Error.MalformedValue("date", ex.getMessage))
    }

}

object ArgParsers extends ArgParsers
