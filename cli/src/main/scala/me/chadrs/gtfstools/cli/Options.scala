package me.chadrs.gtfstools.cli

import java.io.{File, FileInputStream, InputStream}
import java.net.URI
import java.net.http.HttpClient.Redirect
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.{Path, Paths}
import java.util.zip.ZipInputStream
import java.net.http.{HttpClient, HttpRequest}
import scala.jdk.CollectionConverters._
import org.backuity.clist.{Command, arg, opt}

import scala.collection.immutable.SortedSet

object Commands {

  class Hash extends Command(description = "Hash each of the files inside the feed") {
    var algorithm =
      opt[String](description = "hash algorithm to use, i.e. MD5, SHA1, SHA256", default = "md5")

    var feed = arg[GtfsInput](description = "http url or file path to gtfs feed")

  }

  class Dump extends Command(description = "Parse and show a file from inside the feed") {
    var feed = arg[GtfsInput](description = "http url or file path to gtfs feed")
    var file = arg[String](description = "file to read: trips.txt, stop_times.txt, etc.")
  }
}

trait GtfsInput {
  def is: InputStream
  def zis: ZipInputStream = new ZipInputStream(is)
}

object GtfsInput {
  import org.backuity.clist.util.Read
  implicit val gtfsInputFromCli: Read[GtfsInput] = Read.reads[GtfsInput]("path or url") {
    case s if Paths.get(s).toFile.exists() =>
      new GtfsInput {
        override def is: InputStream = new FileInputStream(Paths.get(s).toFile)
      }
    case s if s.startsWith("http") =>
      new GtfsInput {
        override def is: InputStream = {
          val client = HttpClient
            .newBuilder()
            .followRedirects(Redirect.NORMAL)
            .build()
          val resp =
            client.send(HttpRequest.newBuilder(URI.create(s)).build(), BodyHandlers.ofInputStream())
          println(resp)
          resp
            .headers()
            .map()
            .asScala
            .map { case (key, values) => s"$key: ${values.get(0)}" }
            .foreach(println)

          resp.body()
        }
      }

  }
}

case class Options(path: Path, cmd: String, remaining: List[String])

// TODO: replace with case-app when I can figure out positional arguments
object Options {

  val ValidCommands =
    SortedSet("trips", "t", "routes", "r", "agency", "a", "stops", "s", "stoptimes", "st", "raw-st")

  def fromArgs(args: Array[String]): Either[String, Options] = {
    args.toList match {
      case path :: rest if !Paths.get(path).toFile.exists =>
        Left(s"$path not a file")
      case validPath :: cmd :: remaining if !ValidCommands.contains(cmd) =>
        Left(s"unknown command $cmd. Commands are ${ValidCommands.mkString(", ")}")
      case validPath :: validCmd :: remaining =>
        Right(Options(Paths.get(validPath), validCmd, remaining))
    }
  }
}
