package me.chadrs.gtfstools.cli.subcommands

import caseapp.core.RemainingArgs
import caseapp.core.app.CaseApp
import cats.data.ValidatedNec
import cats.implicits._
import me.chadrs.gtfstools.cli.GtfsOptions.Validate
import me.chadrs.gtfstools.cli.{GtfsInput, GtfsZipFile}
import me.chadrs.gtfstools.types.{RoutesFileRow, StopTimesFileRow, TripsFileRow}
import me.chadrs.gtfstools.validators.Validators

object ValidateCmd extends CaseApp[Validate] {
  override def run(options: Validate, remainingArgs: RemainingArgs): Unit = {
    remainingArgs.remaining.map(GtfsInput.fromString).map(_.map(validateGtfs))
  }

  def validateGtfs(gtfsInput: GtfsInput) = {
    val gtfsZip = gtfsInput.toGtfsZipFile
    val extendedValidators = new ExtendedValidators(gtfsZip)

    (validateFile(gtfsZip.agencies, Validators.agency) ++
      validateFile(gtfsZip.stopTimes, extendedValidators.stopTimes) ++
      validateFile(gtfsZip.trips, extendedValidators.trips) ++
      validateFile(gtfsZip.routes, extendedValidators.routes) ++
      validateFile(gtfsZip.calendars, Validators.calendar) ++
      validateFile(gtfsZip.calendarDates, Validators.calendarDates) ++
      validateFile(gtfsZip.shapes, Validators.shapes)).foreach(println)

  }

  def validateFile[R, C](input: Either[String, IndexedSeq[R]], f: R => ValidatedNec[String, C]) =
    input.toValidatedNec
      .andThen(rows => rows.toVector.map(f).sequence)
      .fold(_.iterator, _ => Iterator.empty)

  class ExtendedValidators(gtfsZip: GtfsZipFile) {

    // agency_id field is required when the dataset contains
    // data for multiple transit agencies, otherwise it is optional.

    private val agencies = gtfsZip.agencies
      .getOrElse(Vector.empty)
      .flatMap(_.agencyId.toList.flatten)
      .toSet

    private val shapes = gtfsZip.shapes
      .getOrElse(Vector.empty)
      .flatMap(_.shapeId.toList)
      .toSet

    private val stops = gtfsZip.stops
      .getOrElse(Vector.empty)
      .flatMap(_.stopId.toList)
      .toSet

    private val trips = gtfsZip.trips.getOrElse(Vector.empty).flatMap(_.tripId.toList).toSet

    private val routes = gtfsZip.routes.getOrElse(Vector.empty).flatMap(_.routeId.toList).toSet

    private val services =
      gtfsZip.calendarDates.getOrElse(Vector.empty).flatMap(_.serviceId.toList).toSet ++
        gtfsZip.calendars.getOrElse(Vector.empty).flatMap(_.serviceId.toList).toSet

    def trips(trip: TripsFileRow): ValidatedNec[String, Unit] = {
      val tripId = trip.tripId.map(t => s"Trip $t").getOrElse("Trip with invalid id")
      trip.shapeId
        .flatMap(_.toRight("n/a"))
        .validRef(shapes.contains)
        .leftMap(shapeId => s"$tripId has invalid reference to non-existent shape $shapeId")
        .toValidatedNec |+|
        trip.routeId
          .validRef(routes.contains)
          .leftMap(routeId => s"$tripId has invalid reference to non-existent route $routeId")
          .toValidatedNec |+|
        trip.serviceId
          .validRef(services.contains)
          .leftMap(routeId => s"$tripId has invalid reference to non-existent route $routeId")
          .toValidatedNec |+|
        Validators.trips(trip).map(_ => ())

    }

    def routes(route: RoutesFileRow): ValidatedNec[String, Unit] = {
      val routeId = route.routeId.map(_.toString).getOrElse("Route with invalid id")
      route.agencyId
        .flatMap(_.toRight("n/a"))
        .validRef(agencies.contains)
        .leftMap(id => s"$routeId has invalid reference to agency $id")
        .toValidatedNec |+|
        Either
          .cond(
            agencies.size <= 1 || route.agencyId.exists(_.isDefined),
            (),
            s"$routeId has no agency_id defined which is required with more than one agency in agencies.txt"
          )
          .toValidatedNec |+|
        Validators.routes(route).map(_ => ())
    }

    def stopTimes(stoptime: StopTimesFileRow): ValidatedNec[String, Unit] = {
      stoptime.stopId
        .validRef(stops.contains)
        .leftMap(stop => s"Invalid stop_id $stop referenced in stop_times.txt")
        .toValidatedNec |+|
        stoptime.tripId
          .validRef(trips.contains)
          .leftMap(trip => s"Invalid trip_id $trip referenced in stop_times.txt")
          .toValidatedNec |+|
        Validators.stopTimes(stoptime).map(_ => ())
    }

  }

  implicit class ValidateRefIfExists[L, R](either: Either[L, R]) {

    /**
     * Returns Left(routeId) if the ID is included in the file but does not exist
     * otherwise Right(unit)
     */
    def validRef(exist: R => Boolean): Either[R, Unit] = {
      either
        .fold(_ => Right(()), r => Either.cond(exist(r), (), r))
    }
  }

}
