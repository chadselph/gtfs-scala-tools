package me.chadrs.gtfstools.cli.subcommands

import caseapp.CaseApp
import caseapp.core.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.ServiceOptions
import me.chadrs.gtfstools.cli.calendar.Service
import me.chadrs.gtfstools.cli.{GtfsInput, TablePrinter}
import me.chadrs.gtfstools.types.ServiceId

import java.time.LocalDate
import scala.collection.immutable.SortedSet

object ServiceCmd extends CaseApp[ServiceOptions] {

  override def run(options: ServiceOptions, remainingArgs: RemainingArgs): Unit = {
    def includeService(id: ServiceId) =
      options.serviceId.isEmpty || options.serviceId.contains(id.toString)
    remainingArgs.remaining.map(GtfsInput.fromString).map { feedOrError =>
      feedOrError.left.map(System.err.println) // print any errors
      feedOrError.map { feed =>
        val tableData = Service
          .forGtfs(feed.toGtfsZipFile)
          .collect {
            case (serviceId, service) if includeService(serviceId) =>
              Array(serviceId.toString, formatServiceDates(service.activeDates, options.limit))
          }
          .toArray
        println(TablePrinter.printTableRaw(Seq("service_id", "dates"), tableData, Nil))
      }
    }
  }

  def formatServiceDates(dates: SortedSet[LocalDate], max: Int): String = {
    if (dates.size == 1) dates.mkString("")
    else if (dates.size <= max) dates.grouped(5).map(_.mkString(", ")).mkString("\n")
    else formatServiceDates(dates.take(max - 1), max) + " ... " + dates.last
  }

}
