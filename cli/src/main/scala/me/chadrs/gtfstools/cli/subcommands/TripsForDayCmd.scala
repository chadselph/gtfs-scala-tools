package me.chadrs.gtfstools.cli.subcommands

import caseapp._
import caseapp.core.RemainingArgs
import me.chadrs.gtfstools.cli.{ArgParsers, GtfsInput}
import me.chadrs.gtfstools.cli.GtfsOptions.{TripsPerDayOptions, TripsForDayOptions}
import me.chadrs.gtfstools.cli.calendar.Service
import me.chadrs.gtfstools.types.{TripsFileRow, StopTimesFileRow, ServiceId, Time, TripId}
import ArgParsers.dateArgParser
import me.chadrs.gtfstools.validators.Validators

import java.time.LocalDate

object TripsForDayCmd extends CaseApp[TripsForDayOptions] {
  def run(options: TripsForDayOptions, remainingArgs: RemainingArgs): Unit = {

    remainingArgs.remaining.map(GtfsInput.fromString).map { feedOrError =>
      for {
        feed <- feedOrError.map(_.toGtfsZipFile)
        service = Service.forGtfs(feed)
        trips <- feed.trips.map(groupTripsByServiceId)
        tripFirstStopTimes <- feed.stopTimes.map(getFirstStopTimeByTrip)
      } yield {
        val day = options.day.getOrElse(LocalDate.now())
        val activeServiceIds = service.toVector.collect {
          case (id, dates) if dates.activeDates.contains(day) => id
        }
        val activeTrips = {
          activeServiceIds.flatMap(serviceId => trips.getOrElse(serviceId, IndexedSeq.empty))
        }
        println("time\troute_id\ttrip_id\ttrip_short_name")
        activeTrips
          .map(trip => {
            val start = tripFirstStopTimes.apply(trip.tripId.toOption.get)
            List(
              start,
              trip.routeId.toOption.get,
              trip.tripId.toOption.get,
              trip.tripShortName.toOption.get.getOrElse("")
            ).mkString("\t")
          })
          .sorted
          .foreach(println)
      }
    }
  }

  def getFirstStopTimeByTrip(stopTimes: IndexedSeq[StopTimesFileRow]): Map[TripId, Time] = {

    stopTimes
      .map(st => (st.tripId, st.arrivalTime, st.departureTime))
      .collect {
        case (Right(tripId), _, Right(Some(departure))) =>
          (tripId, departure)
        case (Right(tripId), Right(Some(arrival)), _) =>
          (tripId, arrival)
      }
      .groupMapReduce(_._1)(_._2)(implicitly[Ordering[Time]].min)
  }

  def groupTripsByServiceId(
      trips: IndexedSeq[TripsFileRow]
  ): Map[ServiceId, IndexedSeq[TripsFileRow]] = {
    trips.groupBy(_.serviceId).collect {
      case (Right(serviceId), rows) =>
        serviceId -> rows
    }
  }

}
