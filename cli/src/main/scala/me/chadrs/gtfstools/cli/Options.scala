package me.chadrs.gtfstools.cli

import java.nio.file.{Path, Paths}

import scala.collection.immutable.SortedSet

case class Options(path: Path, cmd: String, remaining: List[String])

// TODO: replace with case-app when I can figure out positional arguments
object Options {

  val ValidCommands = SortedSet("trips", "t", "routes", "r", "agency", "a", "stops", "s", "stoptimes", "st", "raw-st")

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
