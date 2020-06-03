package me.chadrs.gtfstools.cli.subcommands

import caseapp.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.{CommonFileOptions, FileCommandOptions}
import me.chadrs.gtfstools.cli.{GtfsInput, GtfsOptions, TablePrinter}
import me.chadrs.gtfstools.csv.CsvReader
import me.chadrs.gtfstools.types._

import scala.reflect.ClassTag

object FileCmd {

  private val started = System.currentTimeMillis()

  def run(options: FileCommandOptions, remainingArgs: RemainingArgs): Unit = {
    remainingArgs.remaining.map(GtfsInput.fromString).foreach { errorOrFile =>
      errorOrFile
        .flatMap { input =>
          val loadedFile = input.toGtfsZipFile
          options match {
            case GtfsOptions.Trips(args, None) =>
              loadedFile.trips.flatMap { ts =>
                runCommand(ts.map(_.toMap), Trips.Fields, args)
              }
            case GtfsOptions.Trips(args, Some(routeId)) =>
              loadedFile.trips.flatMap { ts =>
                runCommand(
                  ts.filter(_.routeId.contains(RouteId(routeId))).map(_.toMap),
                  Trips.Fields,
                  args
                )
              }
            case GtfsOptions.Routes(args) =>
              loadedFile.parseFile[RoutesFileRow]("routes.txt").flatMap { routes =>
                runCommand(routes.map(_.toMap), Routes.Fields, args)
              }
            case GtfsOptions.Agency(args) =>
              loadedFile.agencies.flatMap { agency =>
                runCommand(agency.map(_.toMap), Agency.Fields, args)
              }
            case GtfsOptions.Stops(args) =>
              loadedFile.parseFile[StopTimesFileRow]("stops.txt").flatMap { stops =>
                runCommand(stops.map(_.toMap), Stops.Fields, args)
              }
            case GtfsOptions.Stoptimes(args, tripSearch) =>
              val tripId = loadedFile.trips.toOption
                .flatMap(_.collectFirst {
                  case trip if trip.tripShortName.contains(tripSearch) => trip.tripId
                })
                .getOrElse(TripId(tripSearch))
              loadedFile.stopTimes.map(st => st.filter(s => s.tripId.contains(tripId))).flatMap { input =>
                runCommand(input.map(_.toMap), StopTimes.Fields, args)
              }
            case GtfsOptions.Calendar(args) =>
              loadedFile.parseFile[CalendarFileRow]("calendar.txt")
                .flatMap(input => runCommand(input.map(_.toMap), Calendar.Fields, args))
            case GtfsOptions.CalendarDates(args) =>
              loadedFile.parseFile[CalendarDatesFileRow]("calendar_dates.txt")
                .flatMap(input => runCommand(input.map(_.toMap), CalendarDates.Fields, args))
          }
        }
        .fold(
          (err: String) => {
            System.err.println(err)
            System.exit(255)
          },
          (s: String) => {
            println(s)
            if (options.args.time) {
              println(s"Took ${System.currentTimeMillis() - started}ms")
            }
          }
        )
    }
  }
  def runCommand[T: ClassTag](
      input: Seq[Map[String, String]],
      headers: Seq[String],
      commonFileOptions: CommonFileOptions
  ): Either[String, String] = {
    if (commonFileOptions.col.forall(headers.contains)) {
      val inputAsArrays = input.map(x => headers.map(x.getOrElse(_, "")).toArray).toArray
      if (commonFileOptions.format.contains("csv")) {
        Left("Unknown format")
      } else {
        Right(TablePrinter.printTableRaw(headers, inputAsArrays, commonFileOptions.col))
      }
    } else {
      val missing = (commonFileOptions.col.toSet diff headers.toSet).mkString(", ")
      Left(s"Invalid columns: $missing. Expected one of: ${headers.mkString(",")}")
    }
  }

}
