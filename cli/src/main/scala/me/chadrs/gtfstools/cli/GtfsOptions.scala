package me.chadrs.gtfstools.cli

import caseapp._

import java.time.LocalDate

sealed trait GtfsOptions

object GtfsOptions extends ArgParsers {

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
  case class CalendarDates(@Recurse args: CommonFileOptions)
      extends GtfsOptions
      with FileCommandOptions
  case class Shapes(@Recurse args: CommonFileOptions, shapeId: Option[String])
      extends GtfsOptions
      with FileCommandOptions

  case class Browse() extends GtfsOptions

  case class Rt() extends GtfsOptions

  case class DrawShape(shapeId: String) extends GtfsOptions

  case class Validate() extends GtfsOptions

  @ProgName("gtfs")
  @CommandName("expires")
  @ArgsName("path/to/feed.zip | http://url/to/feed.zip")
  case class ExpiresOptions(serviceId: Option[String]) extends GtfsOptions

  @ProgName("gtfs")
  @CommandName("service")
  @ArgsName("path/to/feed.zip | http://url/to/feed.zip")
  case class ServiceOptions(
      @HelpMessage("service_ids to include. Defaults to all.") serviceId: List[String],
      @HelpMessage("Max dates to list, default 20") limit: Int = 20
  ) extends GtfsOptions

  @ProgName("gtfs")
  @CommandName("trips-per-day")
  @ArgsName("path/to/feed.zip | http://url/to/feed.zip")
  case class TripsPerDayOptions(
      @HelpMessage("First date to get trips per day of") startDate: Option[LocalDate],
      @HelpMessage("Last date to get trips per day of") endDate: Option[LocalDate],
      @HelpMessage(
        "Ignore calendar_dates outside of the range of the calendars"
      ) ignoreOutsideCalendarDates: Boolean = false
  ) extends GtfsOptions

  @ProgName("gtfs")
  @CommandName("trips-for-day")
  @ArgsName("path/to/feed.zip | http://url/to/feed.zip")
  case class TripsForDayOptions(
      @HelpMessage("Date to get trips of, default to today, local time") day: Option[LocalDate]
  ) extends GtfsOptions

  @ProgName("gtfs")
  @CommandName("fix-zip")
  @ArgsName("path/to/feed.zip | http://url/to/feed.zip")
  case class FixZipOptions(@HelpMessage("Output file name") output: String) extends GtfsOptions

  @ProgName("gtfs")
  @CommandName("compare-static-rt-trips")
  @ArgsName("<path to gtfs> <path to gtfs vehicle-positions>")
  case class CompareStaticRtTrips() extends GtfsOptions
}
