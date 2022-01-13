package me.chadrs.gtfstools.cli.subcommands

import caseapp.core.RemainingArgs
import caseapp.core.app.CaseApp
import cats.data.{NonEmptyChain, ValidatedNec}
import cats.implicits._
import cats.kernel.Order
import me.chadrs.gtfstools.cli.GtfsOptions.Validate
import me.chadrs.gtfstools.parsing.{GtfsInput, GtfsZipFile}
import me.chadrs.gtfstools.types._
import me.chadrs.gtfstools.validators.Validators

object ValidateCmd extends CaseApp[Validate] {
  override def run(options: Validate, remainingArgs: RemainingArgs): Unit = {
    remainingArgs.remaining.map(GtfsInput.fromString).map(_.map(validateGtfs))
  }

  def validateGtfs(gtfsInput: GtfsInput) = {
    val gtfsZip = gtfsInput.toGtfsZipFile
    val extendedValidators = new ExtendedValidators(gtfsZip)

    (validateFile(gtfsZip.agencies, Validators.agency, "agency.txt") ++
      validateFile(gtfsZip.stopTimes, extendedValidators.stopTimes, "stoptimes.txt") ++
      validateFile(gtfsZip.stops, extendedValidators.stops, "stops.txt") ++
      validateFile(gtfsZip.trips, extendedValidators.trips, "trips.txt") ++
      validateFile(gtfsZip.routes, extendedValidators.routes, "routes.txt") ++
      validateFile(gtfsZip.calendars, Validators.calendar, "calendar.txt") ++
      validateFile(gtfsZip.calendarDates, Validators.calendarDates, "calendar_dates.txt") ++
      validateFile(gtfsZip.shapes, Validators.shapes, "shapes.txt") ++
      validateUniqueIds(gtfsZip)).foreach(println)

  }

  def validateUniqueIds(gtfs: GtfsZipFile): Iterator[String] = {
    def prefixError[F](prefix: String)(nec: NonEmptyChain[(F, Int)]): NonEmptyChain[String] =
      nec.map(field => prefix ++ s" found ${field._2} with id ${field._1}")
    val validation =
      validateUniqueField(gtfs.agencies)(_.agencyId).leftMap(prefixError("agency.txt: ")) |+|
        validateUniqueField(gtfs.trips)(_.tripId).leftMap(prefixError("trips.txt: ")) |+|
        validateUniqueField(gtfs.routes)(_.routeId).leftMap(prefixError("routes.txt: ")) |+|
        validateUniqueField(gtfs.stops)(_.stopId).leftMap(prefixError("stops.txt: ")) |+|
        validateUniqueField(gtfs.calendars)(_.serviceId).leftMap(prefixError("calendars.txt: ")) |+|
        validateUniqueField(gtfs.shapes)(
          shape => (shape.shapeId, shape.shapePtSequence).mapN((_, _))
        ).leftMap { dupShapes =>
          implicit val orderShapeId: Order[ShapeId] = Order.by(_.toString)
          val byShapeId = dupShapes.groupBy(_._1._1).map(dups => dups.map(_._1._2))
          NonEmptyChain.fromNonEmptyList(byShapeId.toNel).map {
            case (shapeId, dups) =>
              val samples = dups.toList.take(5).mkString(", ")
              s"shapes.txt: shape $shapeId has ${dups.size} non-unique sequence numbers (examples: $samples)"
          }
        } |+|
        validateUniqueField(gtfs.stopTimes)(st => (st.tripId, st.stopSequence).mapN((_, _)))
          .leftMap { dupStopTimes =>
            val tripsWithDups = dupStopTimes.groupBy(_._1._1)(Order.by(_.toString))
            NonEmptyChain.fromNonEmptyList(tripsWithDups.toNel).map {
              case (tripId, dups) =>
                val samples = dups.map(_._1._2).sorted.toList.take(5).mkString(", ")
                s"stoptimes.txt: $tripId has ${dups.size} non-unique stop_sequence numbers (examples: $samples)"
            }
          }

    // TODO: would be nice if this could return Validated and the errors to iterator[String] could happen all in one place
    validation.fold(_.iterator, _ => Iterator.empty)
  }

  def validateUniqueField[C, F](
      data: Either[String, IndexedSeq[C]]
  )(field: C => Either[String, F]): ValidatedNec[(F, Int), Unit] = {
    val counts =
      data.getOrElse(IndexedSeq.empty).groupMapReduce(field)(_ => 1)(_ + _).collect {
        case (Right(field), count) if count > 1 => (field, count)
      }
    NonEmptyChain
      .fromSeq(counts.toList)
      .toInvalid(())
  }

  def validateFile[R, C](
      input: Either[String, IndexedSeq[R]],
      f: R => ValidatedNec[String, C],
      filename: String
  ) =
    input.toValidatedNec
      .andThen(rows => rows.toVector.map(f).sequence)
      .fold(_.iterator.map(s"$filename: " ++ _), _ => Iterator.empty)

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

    private val stopTimesForTrip = gtfsZip.stopTimes
      .getOrElse(Vector.empty)
      .groupBy(_.tripId)
      .collect {
        case (Right(tripId), stopTimes) => tripId -> stopTimes.sortBy(_.stopSequence.getOrElse(-1))
      }

    def trips(trip: TripsFileRow): ValidatedNec[String, Unit] = {
      val tripIdLabel = trip.tripId.map(t => s"Trip $t").getOrElse("Trip with invalid id")
      val stopTimes =
        trip.tripId.toList.toVector.flatMap(id => stopTimesForTrip.getOrElse(id, Vector.empty))
      val arrivals = (stopTimes.headOption, stopTimes.lastOption)
        .mapN {
          case (firstStop, lastStop) =>
            val firstStopArrival = firstStop.arrivalTime
              .flatMap(_.toRight(s"$tripIdLabel first stop_time is missing required arrival_time"))
              .map(_ => ())
            val lastStopArrival = lastStop.arrivalTime
              .flatMap(_.toRight(s"$tripIdLabel last stop_time is missing required arrival_time"))
              .map(_ => ())
            firstStopArrival.toValidatedNec |+| lastStopArrival.toValidatedNec
        }
        .getOrElse(s"$tripIdLabel has no listed stop_times".invalidNec)
      trip.shapeId
        .flatMap(_.toRight("n/a"))
        .validRef(shapes.contains)
        .leftMap(shapeId => s"$tripIdLabel has invalid reference to non-existent shape $shapeId")
        .toValidatedNec |+|
        trip.routeId
          .validRef(routes.contains)
          .leftMap(routeId => s"$tripIdLabel has invalid reference to non-existent route $routeId")
          .toValidatedNec |+|
        trip.serviceId
          .validRef(services.contains)
          .leftMap(routeId => s"$tripIdLabel has invalid reference to non-existent route $routeId")
          .toValidatedNec |+|
        arrivals |+|
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
      // Logic for "conditionally required" fields
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

    def stops(stop: StopsFileRow): ValidatedNec[String, Unit] = {

      def conditionallyRequired[F](field: StopsFileRow => Either[String, Option[F]], msg: String)(
          isRequired: StopsFileRow => Boolean
      ): Either[String, Unit] =
        Either.cond(field.apply(stop).forall(_.isDefined) || !isRequired.apply(stop), (), msg)

      import LocationType.{BoardingArea, EntranceOrExit, GenericNode, Station, Stop}

      val typesThatRequireName = Set(None, Some(Stop), Some(Station), Some(EntranceOrExit))
      val typesThatRequireParentStation: Set[Option[LocationType]] =
        Set(Some(EntranceOrExit), Some(GenericNode), Some(BoardingArea))

      val stopIdString = stop.stopId.map(stopId => s"stop_id: $stopId").getOrElse("some stop")
      val locationType = stop.locationType.getOrElse(None)
      val (latOpt, lonOpt) = (stop.stopLon.getOrElse(None), stop.stopLat.getOrElse(None))
      val latLonValidation: Either[String, Unit] = (locationType, latOpt, lonOpt) match {
        case (_, Some(_), Some(_)) => Right(())
        case (None | Some(Stop | EntranceOrExit | Station), lon, lat) =>
          val missingFields =
            List(lon.map(_ => "").getOrElse("stop_lon"), lat.map(_ => "").getOrElse("stop_lat"))
              .mkString(" and ")
          Left(s"For $stopIdString, missing required $missingFields.")
        case _ => Right(())
      }
      val stopNameValidation: Either[String, Unit] = conditionallyRequired[String](
        _.stopName,
        s"$stopIdString: stop_name required for locationType"
      ) { stop =>
        stop.locationType.exists(typesThatRequireName.contains)
      }
      val parentStationValidation: Either[String, Unit] =
        conditionallyRequired[StopId](
          _.parentStation,
          s"$stopIdString: parent_station required for locationType"
        ) { stop =>
          stop.locationType.exists(typesThatRequireParentStation.contains)
        }

      latLonValidation.toValidatedNec |+|
        stopNameValidation.toValidatedNec |+|
        parentStationValidation.toValidatedNec |+|
        Validators.stops(stop).map(_ => ())
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
