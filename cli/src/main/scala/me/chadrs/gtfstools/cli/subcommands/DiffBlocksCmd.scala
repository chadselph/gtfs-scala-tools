package me.chadrs.gtfstools.cli.subcommands

import caseapp.CaseApp
import caseapp.core.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.DiffBlocks
import me.chadrs.gtfstools.parsing.GtfsInput

object DiffBlocksCmd extends CaseApp[DiffBlocks] {
  override def run(options: DiffBlocks, remainingArgs: RemainingArgs): Unit = {
    remainingArgs.all match {
      case Seq(gtfs1, gtfs2) =>
        (GtfsInput.fromString(gtfs1), GtfsInput.fromString(gtfs2)) match {

          case (Right(parsedGtfs1), Right(parsedGtfs2)) =>
            for {
              trips1 <- parsedGtfs1.toGtfsZipFile.trips
              trips2 <- parsedGtfs2.toGtfsZipFile.trips
              blocks1 = trips1.map(_.blockId).collect { case Right(Some(block)) => block }.toSet
              blocks2 = trips2.map(_.blockId).collect { case Right(Some(block)) => block }.toSet
            } yield {
              val just1 = blocks1.diff(blocks2)
              val just2 = blocks2.diff(blocks1)
              val both = blocks1.intersect(blocks2)
              println(s"Only first: ${just1.size} Only second: ${just2.size} Both: ${both.size}")
            }
          case (Left(msg), _) => println(msg)
          case (_, Left(msg)) => println(msg)
        }

      case _ => println("usage: DiffBlocksCmd <file1> <file2>")
    }
  }
}
