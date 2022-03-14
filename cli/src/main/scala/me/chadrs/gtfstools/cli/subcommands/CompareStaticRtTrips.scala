package me.chadrs.gtfstools.cli.subcommands

import caseapp.CaseApp
import caseapp.core.RemainingArgs
import com.google.transit.realtime.gtfs_realtime.FeedMessage
import me.chadrs.gtfstools.cli.GtfsOptions.CompareStaticRtTrips
import me.chadrs.gtfstools.parsing.{GtfsInput, GtfsZipFile}
import me.chadrs.gtfstools.types.TripId

object CompareStaticRtTripsCmd extends CaseApp[CompareStaticRtTrips] {

  override def run(options: CompareStaticRtTrips, remainingArgs: RemainingArgs): Unit = {

    if (remainingArgs.remaining.size != 2) {}

    remainingArgs.remaining.map(GtfsInput.fromString) match {
      case Seq(Right(gtfs), Right(gtfsRt)) =>
        compare(gtfs.toGtfsZipFile, gtfsRt.toGtfsRtFeed)
      case Seq(Left(err), _) => System.err.println(err)
      case Seq(_, Left(err)) => System.err.println(err)
      case _ =>
        System.err.println("Expected 2 arguments: gtfs and gtfs-rt")
        System.exit(1)
    }

  }

  def compare(gtfsZipFile: GtfsZipFile, gtfsRtFeed: FeedMessage): Unit = {
    val tripIdsInRt =
      gtfsRtFeed.entity.flatMap(_.vehicle.flatMap(_.trip)).flatMap(_.tripId).map(TripId(_))

    val tripsInStatic = gtfsZipFile.trips
      .fold(s => throw new Exception(s), _.flatMap(_.tripId.toSeq).toSet)

    val (matches, nonMatches) = tripIdsInRt.partition(tripsInStatic.contains)

    val sampleTrips =
      if (nonMatches.nonEmpty) nonMatches.take(10).mkString("Samples: ", ",", ".") else ""
    println(s"""
        |${matches.size} trips match.
        |${nonMatches.size} trips do not. $sampleTrips
        |""".stripMargin)

  }

}
