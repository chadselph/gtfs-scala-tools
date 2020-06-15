package me.chadrs.gtfstools.cli

import caseapp._

sealed trait GtfsOptions

object GtfsOptions {

  @ProgName("gtfs")
  @CommandName("hash")
  @ArgsName("path/to/feed.zip | http://url/to/feed.zip")
  case class HashOptions(
      @HelpMessage("Hash algorithm to use, i.e. MD5, SHA-256")
      algorithm: String = "MD5"
  ) extends GtfsOptions

  case class CommonFileOptions(
      @HelpMessage("Output format. Can be table or csv") format: Option[String],
      @HelpMessage("Columns to include") col: List[String],
      @HelpMessage("Log how long it took") time: Boolean = false
  )

  sealed trait FileCommandOptions {
    def args: CommonFileOptions
  }
  case class Trips(@Recurse args: CommonFileOptions, route: Option[String])
      extends GtfsOptions
      with FileCommandOptions
  case class Routes(@Recurse args: CommonFileOptions) extends GtfsOptions with FileCommandOptions
  case class Agency(@Recurse args: CommonFileOptions) extends GtfsOptions with FileCommandOptions
  case class Stops(@Recurse args: CommonFileOptions) extends GtfsOptions with FileCommandOptions
  case class Stoptimes(@Recurse args: CommonFileOptions, trip: String)
      extends GtfsOptions
      with FileCommandOptions
  case class Calendar(@Recurse args: CommonFileOptions) extends GtfsOptions with FileCommandOptions
  case class CalendarDates(@Recurse args: CommonFileOptions) extends GtfsOptions with FileCommandOptions

  case class Browse() extends GtfsOptions

}
