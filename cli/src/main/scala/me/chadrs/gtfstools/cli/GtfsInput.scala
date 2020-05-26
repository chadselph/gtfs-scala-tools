package me.chadrs.gtfstools.cli

import java.io.{FileInputStream, InputStream}
import java.net.URI
import java.net.http.HttpClient.Redirect
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.{Path, Paths}
import java.util.zip.ZipInputStream

import me.chadrs.gtfstools.csv.{CsvParser, CsvReader}
import me.chadrs.gtfstools.types.{
  Agency, Calendar, CalendarDates, RouteId, Routes, StopTimes, Stops, Trips
}

import scala.jdk.CollectionConverters._
import scala.util.Try
import better.files._

trait GtfsInput {
  def is: InputStream
  def description: String
  def zis: ZipInputStream = new ZipInputStream(is)
  def toGtfsZipFile: GtfsZipFile = new GtfsZipFile(is)
}

object GtfsInput {
  private val client = HttpClient
    .newBuilder()
    .followRedirects(Redirect.NORMAL)
    .build()

  def fromString(s: String): Either[String, GtfsInput] =
    s match {
      case ExistingPath(p) =>
        Right(new GtfsInput {
          override def description: String = p.toAbsolutePath.toString
          override def is: InputStream = new FileInputStream(p.toFile)
        })
      case HttpUri(uri) =>
        Right(new GtfsInput {
          private lazy val resp: HttpResponse[InputStream] = client
            .send(HttpRequest.newBuilder(uri).build(), BodyHandlers.ofInputStream())
          override def is: InputStream = {
            resp.body()
          }
          override def description: String = {
            val headers = resp
              .headers()
              .map()
              .asScala
              .map { case (key, values) => s"$key: ${values.get(0)}" }
            s"""
               |${resp.request().method()} ${resp.request().uri()}
               |${resp.version()} ${resp.statusCode()}
               |${headers.mkString("\n")}
               |""".stripMargin
          }
        })
      case s =>
        Left(s"$s does not exist")

    }

  /**
   * Extractors
   */
  object ExistingPath {
    def unapply(arg: String): Option[Path] = {
      val path = Paths.get(arg)
      if (path.toFile.exists()) {
        Some(path)
      } else None
    }
  }
  object HttpUri {
    val schemes = Set("http", "https")
    def unapply(arg: String): Option[URI] = {
      Try(URI.create(arg)).toOption
        .filter(uri => schemes.contains(uri.getScheme))
    }
  }

  def unapply(arg: String): Option[GtfsInput] = fromString(arg).toOption
}

class GtfsZipFile(inputStream: InputStream) {

  val files: Map[String, Array[Byte]] = {
    val zis = inputStream.asZipInputStream
    zis.mapEntries { entry =>
      entry.getName -> zis.readAllBytes()
    }.toMap
  }

  def rawFile(path: String): Either[String, String] = {
    files
      .get(path)
      .toRight(s"$path is missing")
      .flatMap(CsvParser.parseFile)
      .left
      .map(_.toString)
      .map(csv => csv.headers.mkString(",") ++ csv.rows.map(_.mkString(",")).mkString("\n"))
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
