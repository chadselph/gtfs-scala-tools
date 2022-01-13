package me.chadrs.gtfstools.cli.subcommands

import caseapp.CaseApp
import caseapp.core.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.ExpiresOptions
import me.chadrs.gtfstools.parsing.GtfsInput
import me.chadrs.gtfstools.types.{CalendarDatesFileRow, ExceptionType, CalendarFileRow, ServiceId}

import java.time.LocalDate

object ExpiresCmd extends CaseApp[ExpiresOptions] {

  override def run(options: ExpiresOptions, remainingArgs: RemainingArgs): Unit = {
    remainingArgs.remaining.map(GtfsInput.fromString).map { feedOrError =>
      feedOrError.left.map(System.err.println) // print any errors
      feedOrError.map { feed =>
        getLastServiceDate(feed, options.serviceId.map(ServiceId(_)))
          .fold(ifEmpty = println("No service found.")) { endDate =>
            println(s"Last service date is $endDate")
          }
      }
    }
  }

  def getLastServiceDate(feed: GtfsInput, optServiceId: Option[ServiceId]): Option[LocalDate] = {
    val gtfs = feed.toGtfsZipFile
    val datesFields = gtfs.calendars
      .getOrElse(Vector.empty)
      .filter(calHasService(optServiceId))
      .map(_.endDate) ++
      gtfs.calendarDates
        .getOrElse(Vector.empty)
        .filter(calDateHasService(optServiceId))
        .filter(row => row.exceptionType.exists(_ == ExceptionType(1))) // Added service
        .map(_.date)

    val validateDates = datesFields.collect { case Right(d) => d }
    datesFields.foreach {
      case Left(err) => System.err.println(s"$err")
      case _         =>
    }
    validateDates.maxOption
  }

  private def calHasService(serviceId: Option[ServiceId])(cal: CalendarFileRow) =
    serviceId.fold(true)(optService => cal.serviceId.exists(_ == optService))
  private def calDateHasService(serviceId: Option[ServiceId])(cal: CalendarDatesFileRow) =
    serviceId.fold(true)(optService => cal.serviceId.exists(_ == optService))
}
