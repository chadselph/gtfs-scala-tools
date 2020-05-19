package me.chadrs.gtfstools.cli

import java.nio.file.{Path, Paths}

import better.files._
import me.chadrs.gtfstools.csv.{CsvParser, CsvReader}
import me.chadrs.gtfstools.types.{Agency, Calendar, CalendarDates, RouteId, Routes, StopTimes, Stops, TripId, Trips}

import scala.reflect.ClassTag

object GtfsCli {

  // TODO: better arg parsing, faster csv parsing, lazier loading, validation

  def main(args: Array[String]): Unit = {
    Options.fromArgs(args).flatMap { case Options(path, cmd, remaining) =>
      val loadedFile = new GtfsZipFile(path)
      (cmd, remaining) match {
        case ("agency" | "a", Nil) =>
          runCommand(loadedFile.agencies, Agency.Fields)
        case ("routes" | "r", Nil) =>
          runCommand(loadedFile.routes, Routes.Fields)
        case ("trips" | "t", List(routeId)) =>
          runCommand(loadedFile.tripsForRoute(routeId), Trips.Fields)
        case ("trips" | "t", Nil) =>
          runCommand(loadedFile.trips, Trips.Fields)
        case ("stoptimes" | "st", List(tripSearch)) =>
          val tripId = loadedFile.trips.toOption.flatMap(_.collectFirst {
            case trip if trip.tripShortName.contains(tripSearch) => trip.tripId
          }).getOrElse(TripId(tripSearch))
          runCommand(
            loadedFile.stopTimes.map(st => st.filter(s => s.tripId == tripId)),
            StopTimes.Fields
          )
        case ("raw-st", Nil) =>
          loadedFile.rawFile("stop_times.txt")
        case (cmd, Nil) =>
          Left(s"Expected argument for $cmd")
        case (cmd, args) =>
          Left(s"Unexpected argument for $cmd")
      }
    }.fold(err => {
      System.err.println(err)
      System.exit(1)
    }, println)
  }

  def runCommand[T <: Product : ClassTag](input: CsvReader.Result[Seq[T]], headers: Seq[String]): Either[String, String] = {
    // TODO: formatting options
    input.map(s => TablePrinter.printTable(headers ++ Array("Extra"), s, Set.empty))
  }

}

class GtfsZipFile(filePath: Path) {

  val files: Map[String, Array[Byte]] = {
    File(filePath).zipInputStream() { zis =>
      zis.mapEntries { entry =>
        entry.getName -> zis.readAllBytes()
      }.toMap
    }
  }

  def rawFile(path: String): Either[String, String] = {
    files.get(path).toRight(s"$path is missing")
      .flatMap(CsvParser.parseFile).left.map(_.toString).map(csv => csv.headers.mkString(",") ++ csv.rows.map(_.mkString(",")).mkString("\n"))
  }

  def parseFile[T: CsvReader](path: String): Either[String, Seq[T]] = {
    files
      .get(path)
      .toRight(s"$path is missing")
      .flatMap(CsvParser.parseFile)
      .map(CsvReader.parseAs[T])
      .map { lines =>
        lines.collect {
          case Left(err) => println(s"Warning $err")
        }
        lines.collect {
          case Right(t) => t
        }
      }
      .left
      .map(_.toString())
  }

  lazy val trips = parseFile[Trips]("trips.txt")
  lazy val stopTimes = parseFile[StopTimes]("stop_times.txt")
  lazy val routes = parseFile[Routes]("routes.txt")
  lazy val stops = parseFile[Stops]("stops.txt")
  lazy val agencies = parseFile[Agency]("agency.txt")
  lazy val calendarDates = parseFile[CalendarDates]("calendar_dates.txt")
  lazy val calendar = parseFile[Calendar]("calendar.txt")

  def tripsForRoute(routeId: String): Either[String, Seq[Trips]] = {
    trips.map { t => t.filter(_.routeId == RouteId(routeId)) }
  }

}
