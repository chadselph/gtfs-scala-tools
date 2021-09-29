package me.chadrs.gtfstools.cli.subcommands

import caseapp._
import me.chadrs.gtfstools.cli.{ArgParsers, GtfsInput}
import caseapp.core.RemainingArgs
import me.chadrs.gtfstools.cli.GtfsOptions.TripsPerDayOptions
import me.chadrs.gtfstools.cli.calendar.Service
import me.chadrs.gtfstools.types.TripsFileRow
import ArgParsers.dateArgParser // used by macro-generated code

import java.time.LocalDate

object TripsPerDayCmd extends CaseApp[TripsPerDayOptions] {
  def run(options: TripsPerDayOptions, remainingArgs: RemainingArgs): Unit = {

    remainingArgs.remaining.map(GtfsInput.fromString).map { feedOrError =>
      for {
        feed <- feedOrError.map(_.toGtfsZipFile)
        service = Service.forGtfs(feed)
        trips <- feed.trips.map(groupTripsByServiceId)
      } yield {
        val calendars = service.values.map(_.calendar).collect { case Some(calendar) => calendar }
        val (minCalDate, maxCalDate) =
          (calendars.map(_.startDate).min, calendars.map(_.endDate).max)
        def isOutsideDate(date: LocalDate): Boolean =
          options.ignoreOutsideCalendarDates && (date.isAfter(maxCalDate) || date
            .isBefore(minCalDate))

        val datesAndServiceId = service.toVector.flatMap {
          case (id, dates) => dates.activeDates.filter(!isOutsideDate(_)).toVector.map(_ -> id)
        }
        val tripIdsByDate = datesAndServiceId
          .groupMapReduce(_._1)(row => trips.getOrElse(row._2, IndexedSeq.empty))(_ ++ _)
        val (min, max) = (
          options.startDate.getOrElse(tripIdsByDate.keys.min),
          options.endDate.getOrElse(tripIdsByDate.keys.max)
        )
        min
          .datesUntil(max.plusDays(1))
          .forEach(date => {
            println(s"$date - ${tripIdsByDate.getOrElse(date, Nil).size} trips")
          })
      }
    }
  }

  def groupTripsByServiceId(trips: IndexedSeq[TripsFileRow]) = {
    trips.groupBy(_.serviceId).collect {
      case (Right(serviceId), rows) =>
        serviceId -> rows.map(_.tripId).collect {
          case Right(tripId) => tripId
        }
    }
  }

}
