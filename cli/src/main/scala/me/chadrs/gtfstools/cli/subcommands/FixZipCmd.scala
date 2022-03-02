package me.chadrs.gtfstools.cli.subcommands

import caseapp.CaseApp
import caseapp.core.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.FixZipOptions
import me.chadrs.gtfstools.parsing.GtfsInput
import better.files._

import java.io.FileOutputStream
import java.util.zip.{ZipOutputStream, ZipEntry}

object FixZipCmd extends CaseApp[FixZipOptions] {

  val searchFiles = Set(
    "agency.txt",
    "calendar_dates.txt",
    "calendar.txt",
    "routes.txt",
    "shapes.txt",
    "stop_times.txt",
    "stops.txt",
    "trips.txt"
  )

  override def run(options: FixZipOptions, remainingArgs: RemainingArgs): Unit = {
    val fileOutputStream = new FileOutputStream(options.output)
    val gtfsArg = remainingArgs.remaining match {
      case Seq(gtfs) => GtfsInput.fromString(gtfs)
      case _         => Left("Expected exactly one gtfs url or file path")
    }
    gtfsArg
      .flatMap { gtfsInput =>
        val zipOut = new ZipOutputStream(fileOutputStream)
        val zipInput = gtfsInput.zis
        zipInput.mapEntries { entry =>
          val targetPathSearch = searchFiles.find(
            searchFile =>
              !entry.getName.startsWith("__MACOSX") && entry.getName.endsWith(searchFile)
          )
          targetPathSearch.fold {
            System.err.println(s"Skipping ${entry.getName}")
          } { targetPath =>
            System.err.println(s"Writing ${entry.getName} to $targetPath")
            zipOut.putNextEntry(new ZipEntry(targetPath))
            zipInput.transferTo(zipOut)
            zipOut.closeEntry()

          }
        }.toList
        zipOut.finish()
        zipOut.close()
        Right("Done.")
      }
      .fold(System.err.println, println)
  }
}
