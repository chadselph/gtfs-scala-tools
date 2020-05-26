package me.chadrs.gtfstools.cli.subcommands

import caseapp.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.{CommonFileOptions, FileCommandOptions}
import me.chadrs.gtfstools.cli.{GtfsInput, GtfsOptions, TablePrinter}
import me.chadrs.gtfstools.csv.CsvReader
import me.chadrs.gtfstools.types._

import scala.reflect.ClassTag

object FileCmd {

  def run(options: FileCommandOptions, remainingArgs: RemainingArgs): Unit = {
    remainingArgs.remaining.map(GtfsInput.fromString).foreach { errorOrFile =>
      errorOrFile
        .flatMap { input =>
          val loadedFile = input.toGtfsZipFile
          options match {
            case GtfsOptions.Trips(args, routeId) =>
              routeId.fold(ifEmpty = runCommand(loadedFile.trips, Trips.Fields, args)) { routeId =>
                runCommand(loadedFile.tripsForRoute(routeId), Trips.Fields, args)
              }
            case GtfsOptions.Routes(args) =>
              runCommand(loadedFile.routes, Routes.Fields, args)
            case GtfsOptions.Agency(args) =>
              runCommand(loadedFile.agencies, Agency.Fields, args)
            case GtfsOptions.Stops(args) =>
              runCommand(loadedFile.stops, Stops.Fields, args)
            case GtfsOptions.Stoptimes(args, tripSearch) =>
              val tripId = loadedFile.trips.toOption
                .flatMap(_.collectFirst {
                  case trip if trip.tripShortName.contains(tripSearch) => trip.tripId
                })
                .getOrElse(TripId(tripSearch))
              runCommand(
                loadedFile.stopTimes.map(st => st.filter(s => s.tripId == tripId)),
                StopTimes.Fields,
                args
              )
          }
        }
        .fold(println, println)
    }
  }

  def runCommand[T <: Product: ClassTag](
      input: CsvReader.Result[Seq[T]],
      headers: Seq[String],
      commonFileOptions: CommonFileOptions
  ): Either[String, String] = {
    if (commonFileOptions.col.forall(headers.contains)) {
      input.map(s => TablePrinter.printTable(headers ++ Array("Extra"), s, commonFileOptions.col))
    } else {
      val missing = (commonFileOptions.col.toSet diff headers.toSet).mkString(", ")
      Left(s"Invalid columns: $missing. Expected one of: ${headers.mkString(",")}")
    }
  }

}
