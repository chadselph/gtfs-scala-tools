package me.chadrs.gtfstools.parsing

import better.files._
import com.google.transit.realtime.gtfs_realtime.FeedMessage
import me.chadrs.gtfstools.csv.CsvRowViewer
import me.chadrs.gtfstools.types._

import java.io.{InputStream, FileInputStream}
import java.net.URI
import java.net.http.HttpClient.Redirect
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpRequest, HttpClient, HttpResponse}
import java.nio.file.{Path, Paths}
import java.util.zip.ZipInputStream
import scala.jdk.CollectionConverters._
import scala.util.Try

trait GtfsInput {
  def is: InputStream
  def description: String
  def zis: ZipInputStream = new ZipInputStream(is)
  def toGtfsZipFile: GtfsZipFile = new GtfsZipFile(is)
  def toGtfsRtFeed: FeedMessage = FeedMessage.parseFrom(is)
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

  lazy val loadFiles: Map[String, Array[Byte]] = {
    val zis = inputStream.asZipInputStream
    zis.mapEntries { entry =>
      entry.getName -> zis.readAllBytes()
    }.toMap
  }

  def loadFile(path: String): Either[String, Array[Byte]] =
    loadFiles.get(path).toRight(s"$path is missing")

  def parseFile[T: CsvRowViewer](path: String): Either[String, IndexedSeq[T]] = {
    loadFile(path)
      .map(UnivocityCsvParser.parseFile)
      .map(CsvRowViewer.mapFile[T])
  }

  lazy val agencies = parseFile[AgencyFileRow]("agency.txt")
  lazy val calendarDates = parseFile[CalendarDatesFileRow]("calendar_dates.txt")
  lazy val calendars = parseFile[CalendarFileRow]("calendar.txt")
  lazy val routes = parseFile[RoutesFileRow]("routes.txt")
  lazy val shapes = parseFile[ShapesFileRow]("shapes.txt")
  lazy val stopTimes = parseFile[StopTimesFileRow]("stop_times.txt")
  lazy val stops = parseFile[StopsFileRow]("stops.txt")
  lazy val trips = parseFile[TripsFileRow]("trips.txt")

  def tripsForRoute(routeId: String): Either[String, Seq[TripsFileRow]] = {
    trips.map { t => t.filter(_.routeId.contains(RouteId(routeId))) }
  }

}
