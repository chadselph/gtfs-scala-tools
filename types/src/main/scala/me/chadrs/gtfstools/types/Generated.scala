package me.chadrs.gtfstools.types

import me.chadrs.gtfstools.csv.{CsvCursor, CsvFromString, CsvReader}

case class Timepoint(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object Timepoint {
  implicit val csvFromString: CsvFromString[Timepoint] =
    implicitly[CsvFromString[Int]].map(Timepoint.apply)
}
case class PickupType(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object PickupType {
  implicit val csvFromString: CsvFromString[PickupType] =
    implicitly[CsvFromString[Int]].map(PickupType.apply)
}
case class TripId(override val toString: String) extends AnyVal
object TripId {
  implicit val csvFromString: CsvFromString[TripId] =
    CsvFromString.stringFromString.map(TripId.apply)
}
case class TransferType(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object TransferType {
  implicit val csvFromString: CsvFromString[TransferType] =
    implicitly[CsvFromString[Int]].map(TransferType.apply)
}
case class DirectionId(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object DirectionId {
  implicit val csvFromString: CsvFromString[DirectionId] =
    implicitly[CsvFromString[Int]].map(DirectionId.apply)
}
case class PathwayMode(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object PathwayMode {
  implicit val csvFromString: CsvFromString[PathwayMode] =
    implicitly[CsvFromString[Int]].map(PathwayMode.apply)
}
case class TableName(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object TableName {
  implicit val csvFromString: CsvFromString[TableName] =
    implicitly[CsvFromString[Int]].map(TableName.apply)
}
case class ShapeId(override val toString: String) extends AnyVal
object ShapeId {
  implicit val csvFromString: CsvFromString[ShapeId] =
    CsvFromString.stringFromString.map(ShapeId.apply)
}
case class RecordId(override val toString: String) extends AnyVal
object RecordId {
  implicit val csvFromString: CsvFromString[RecordId] =
    CsvFromString.stringFromString.map(RecordId.apply)
}
case class PaymentMethod(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object PaymentMethod {
  implicit val csvFromString: CsvFromString[PaymentMethod] =
    implicitly[CsvFromString[Int]].map(PaymentMethod.apply)
}
case class RouteId(override val toString: String) extends AnyVal
object RouteId {
  implicit val csvFromString: CsvFromString[RouteId] =
    CsvFromString.stringFromString.map(RouteId.apply)
}
case class ExceptionType(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object ExceptionType {
  implicit val csvFromString: CsvFromString[ExceptionType] =
    implicitly[CsvFromString[Int]].map(ExceptionType.apply)
}
case class AttributionId(override val toString: String) extends AnyVal
object AttributionId {
  implicit val csvFromString: CsvFromString[AttributionId] =
    CsvFromString.stringFromString.map(AttributionId.apply)
}
case class WheelchairAccessible(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object WheelchairAccessible {
  implicit val csvFromString: CsvFromString[WheelchairAccessible] =
    implicitly[CsvFromString[Int]].map(WheelchairAccessible.apply)
}
case class WheelchairBoarding(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object WheelchairBoarding {
  implicit val csvFromString: CsvFromString[WheelchairBoarding] =
    implicitly[CsvFromString[Int]].map(WheelchairBoarding.apply)
}
case class BlockId(override val toString: String) extends AnyVal
object BlockId {
  implicit val csvFromString: CsvFromString[BlockId] =
    CsvFromString.stringFromString.map(BlockId.apply)
}
case class PathwayId(override val toString: String) extends AnyVal
object PathwayId {
  implicit val csvFromString: CsvFromString[PathwayId] =
    CsvFromString.stringFromString.map(PathwayId.apply)
}
case class FareId(override val toString: String) extends AnyVal
object FareId {
  implicit val csvFromString: CsvFromString[FareId] =
    CsvFromString.stringFromString.map(FareId.apply)
}
case class ServiceId(override val toString: String) extends AnyVal
object ServiceId {
  implicit val csvFromString: CsvFromString[ServiceId] =
    CsvFromString.stringFromString.map(ServiceId.apply)
}
case class AgencyId(override val toString: String) extends AnyVal
object AgencyId {
  implicit val csvFromString: CsvFromString[AgencyId] =
    CsvFromString.stringFromString.map(AgencyId.apply)
}
case class RecordSubId(override val toString: String) extends AnyVal
object RecordSubId {
  implicit val csvFromString: CsvFromString[RecordSubId] =
    CsvFromString.stringFromString.map(RecordSubId.apply)
}
case class LocationType(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object LocationType {
  implicit val csvFromString: CsvFromString[LocationType] =
    implicitly[CsvFromString[Int]].map(LocationType.apply)
}
case class DropOffType(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object DropOffType {
  implicit val csvFromString: CsvFromString[DropOffType] =
    implicitly[CsvFromString[Int]].map(DropOffType.apply)
}
case class RouteType(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object RouteType {
  implicit val csvFromString: CsvFromString[RouteType] =
    implicitly[CsvFromString[Int]].map(RouteType.apply)
}
case class LevelId(override val toString: String) extends AnyVal
object LevelId {
  implicit val csvFromString: CsvFromString[LevelId] =
    CsvFromString.stringFromString.map(LevelId.apply)
}
case class ExactTimes(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object ExactTimes {
  implicit val csvFromString: CsvFromString[ExactTimes] =
    implicitly[CsvFromString[Int]].map(ExactTimes.apply)
}
case class StopId(override val toString: String) extends AnyVal
object StopId {
  implicit val csvFromString: CsvFromString[StopId] =
    CsvFromString.stringFromString.map(StopId.apply)
}
case class ZoneId(override val toString: String) extends AnyVal
object ZoneId {
  implicit val csvFromString: CsvFromString[ZoneId] =
    CsvFromString.stringFromString.map(ZoneId.apply)
}
case class BikesAllowed(toValue: Int) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object BikesAllowed {
  implicit val csvFromString: CsvFromString[BikesAllowed] =
    implicitly[CsvFromString[Int]].map(BikesAllowed.apply)
}
case class Longitude(toValue: Double) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object Longitude {
  implicit val csvFromString: CsvFromString[Longitude] =
    implicitly[CsvFromString[Double]].map(Longitude.apply)
}
case class Latitude(toValue: Double) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object Latitude {
  implicit val csvFromString: CsvFromString[Latitude] =
    implicitly[CsvFromString[Double]].map(Latitude.apply)
}
case class Color(toValue: String) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object Color {
  implicit val csvFromString: CsvFromString[Color] =
    implicitly[CsvFromString[String]].map(Color.apply)
}
case class LanguageCode(toValue: String) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object LanguageCode {
  implicit val csvFromString: CsvFromString[LanguageCode] =
    implicitly[CsvFromString[String]].map(LanguageCode.apply)
}
case class CurrencyCode(toValue: String) extends AnyVal {
  override def toString: String = {
    toValue.toString
  }
}
object CurrencyCode {
  implicit val csvFromString: CsvFromString[CurrencyCode] =
    implicitly[CsvFromString[String]].map(CurrencyCode.apply)
}
case class Agency(
    agencyId: Option[AgencyId],
    agencyName: String,
    agencyUrl: java.net.URI,
    agencyTimezone: java.time.ZoneId,
    agencyLang: Option[LanguageCode],
    agencyPhone: Option[String],
    agencyFareUrl: Option[java.net.URI],
    agencyEmail: Option[String],
    extra: Map[String, String] = Map.empty
)
case class Stops(
    stopId: StopId,
    stopCode: Option[String],
    stopName: Option[String],
    stopDesc: Option[String],
    stopLat: Option[Latitude],
    stopLon: Option[Longitude],
    zoneId: Option[ZoneId],
    stopUrl: Option[java.net.URI],
    locationType: Option[LocationType],
    parentStation: Option[StopId],
    stopTimezone: Option[java.time.ZoneId],
    wheelchairBoarding: Option[WheelchairBoarding],
    levelId: Option[LevelId],
    platformCode: Option[String],
    extra: Map[String, String] = Map.empty
)
case class Routes(
    routeId: RouteId,
    agencyId: Option[AgencyId],
    routeShortName: Option[String],
    routeLongName: Option[String],
    routeDesc: Option[String],
    routeType: RouteType,
    routeUrl: Option[java.net.URI],
    routeColor: Option[Color],
    routeTextColor: Option[Color],
    routeSortOrder: Option[Int],
    extra: Map[String, String] = Map.empty
)
case class Trips(
    routeId: RouteId,
    serviceId: ServiceId,
    tripId: TripId,
    tripHeadsign: Option[String],
    tripShortName: Option[String],
    directionId: Option[DirectionId],
    blockId: Option[BlockId],
    shapeId: Option[ShapeId],
    wheelchairAccessible: Option[WheelchairAccessible],
    bikesAllowed: Option[BikesAllowed],
    extra: Map[String, String] = Map.empty
)
case class StopTimes(
    tripId: TripId,
    arrivalTime: Option[java.time.LocalTime],
    departureTime: Option[java.time.LocalTime],
    stopId: StopId,
    stopSequence: Int,
    stopHeadsign: Option[String],
    pickupType: Option[PickupType],
    dropOffType: Option[DropOffType],
    shapeDistTraveled: Option[Double],
    timepoint: Option[Timepoint],
    extra: Map[String, String] = Map.empty
)
case class Calendar(
    serviceId: ServiceId,
    monday: Boolean,
    tuesday: Boolean,
    wednesday: Boolean,
    thursday: Boolean,
    friday: Boolean,
    saturday: Boolean,
    sunday: Boolean,
    startDate: java.time.LocalDate,
    endDate: java.time.LocalDate,
    extra: Map[String, String] = Map.empty
)
case class CalendarDates(
    serviceId: ServiceId,
    date: java.time.LocalDate,
    exceptionType: ExceptionType,
    extra: Map[String, String] = Map.empty
)
case class FareAttributes(
    fareId: FareId,
    price: Double,
    currencyType: CurrencyCode,
    paymentMethod: PaymentMethod,
    transfers: String,
    agencyId: Option[AgencyId],
    transferDuration: Option[Int],
    extra: Map[String, String] = Map.empty
)
case class FareRules(
    fareId: FareId,
    routeId: Option[RouteId],
    originId: Option[ZoneId],
    destinationId: Option[ZoneId],
    containsId: Option[ZoneId],
    extra: Map[String, String] = Map.empty
)
case class Shapes(
    shapeId: ShapeId,
    shapePtLat: Latitude,
    shapePtLon: Longitude,
    shapePtSequence: Int,
    shapeDistTraveled: Option[Double],
    extra: Map[String, String] = Map.empty
)
case class Frequencies(
    tripId: TripId,
    startTime: java.time.LocalTime,
    endTime: java.time.LocalTime,
    headwaySecs: Int,
    exactTimes: Option[ExactTimes],
    extra: Map[String, String] = Map.empty
)
case class Transfers(
    fromStopId: StopId,
    toStopId: StopId,
    transferType: TransferType,
    minTransferTime: Option[Int],
    extra: Map[String, String] = Map.empty
)
case class Pathways(
    pathwayId: PathwayId,
    fromStopId: StopId,
    toStopId: StopId,
    pathwayMode: PathwayMode,
    isBidirectional: Boolean,
    length: Option[Double],
    traversalTime: Option[Int],
    stairCount: Option[Int],
    maxSlope: Option[Double],
    minWidth: Option[Double],
    signpostedAs: Option[String],
    reversedSignpostedAs: Option[String],
    extra: Map[String, String] = Map.empty
)
case class Levels(
    levelId: LevelId,
    levelIndex: Double,
    levelName: Option[String],
    extra: Map[String, String] = Map.empty
)
case class FeedInfo(
    feedPublisherName: String,
    feedPublisherUrl: java.net.URI,
    feedLang: LanguageCode,
    defaultLang: Option[LanguageCode],
    feedStartDate: Option[java.time.LocalDate],
    feedEndDate: Option[java.time.LocalDate],
    feedVersion: Option[String],
    feedContactEmail: Option[String],
    feedContactUrl: Option[java.net.URI],
    extra: Map[String, String] = Map.empty
)
case class Translations(
    tableName: TableName,
    fieldName: String,
    language: LanguageCode,
    translation: String,
    recordId: Option[RecordId],
    recordSubId: Option[RecordSubId],
    fieldValue: Option[String],
    extra: Map[String, String] = Map.empty
)
case class Attributions(
    attributionId: Option[AttributionId],
    agencyId: Option[AgencyId],
    routeId: Option[RouteId],
    tripId: Option[TripId],
    organizationName: String,
    isProducer: Option[Boolean],
    isOperator: Option[Boolean],
    isAuthority: Option[Boolean],
    attributionUrl: Option[java.net.URI],
    attributionEmail: Option[String],
    attributionPhone: Option[String],
    extra: Map[String, String] = Map.empty
)
object Agency {
  implicit val csvReader: CsvReader[Agency] = (c: CsvCursor) =>
    for {
      agencyId <- c.optionally[AgencyId]("agency_id")
      agencyName <- c.required[String]("agency_name")
      agencyUrl <- c.required[java.net.URI]("agency_url")
      agencyTimezone <- c.required[java.time.ZoneId]("agency_timezone")
      agencyLang <- c.optionally[LanguageCode]("agency_lang")
      agencyPhone <- c.optionally[String]("agency_phone")
      agencyFareUrl <- c.optionally[java.net.URI]("agency_fare_url")
      agencyEmail <- c.optionally[String]("agency_email")
    } yield Agency.apply(
      agencyId,
      agencyName,
      agencyUrl,
      agencyTimezone,
      agencyLang,
      agencyPhone,
      agencyFareUrl,
      agencyEmail
    )
  val Fields: Seq[String] = Seq(
    "agency_id",
    "agency_name",
    "agency_url",
    "agency_timezone",
    "agency_lang",
    "agency_phone",
    "agency_fare_url",
    "agency_email"
  )
}
object Stops {
  implicit val csvReader: CsvReader[Stops] = (c: CsvCursor) =>
    for {
      stopId <- c.required[StopId]("stop_id")
      stopCode <- c.optionally[String]("stop_code")
      stopName <- c.optionally[String]("stop_name")
      stopDesc <- c.optionally[String]("stop_desc")
      stopLat <- c.optionally[Latitude]("stop_lat")
      stopLon <- c.optionally[Longitude]("stop_lon")
      zoneId <- c.optionally[ZoneId]("zone_id")
      stopUrl <- c.optionally[java.net.URI]("stop_url")
      locationType <- c.optionally[LocationType]("location_type")
      parentStation <- c.optionally[StopId]("parent_station")
      stopTimezone <- c.optionally[java.time.ZoneId]("stop_timezone")
      wheelchairBoarding <- c.optionally[WheelchairBoarding]("wheelchair_boarding")
      levelId <- c.optionally[LevelId]("level_id")
      platformCode <- c.optionally[String]("platform_code")
    } yield Stops.apply(
      stopId,
      stopCode,
      stopName,
      stopDesc,
      stopLat,
      stopLon,
      zoneId,
      stopUrl,
      locationType,
      parentStation,
      stopTimezone,
      wheelchairBoarding,
      levelId,
      platformCode
    )
  val Fields: Seq[String] = Seq(
    "stop_id",
    "stop_code",
    "stop_name",
    "stop_desc",
    "stop_lat",
    "stop_lon",
    "zone_id",
    "stop_url",
    "location_type",
    "parent_station",
    "stop_timezone",
    "wheelchair_boarding",
    "level_id",
    "platform_code"
  )
}
object Routes {
  implicit val csvReader: CsvReader[Routes] = (c: CsvCursor) =>
    for {
      routeId <- c.required[RouteId]("route_id")
      agencyId <- c.optionally[AgencyId]("agency_id")
      routeShortName <- c.optionally[String]("route_short_name")
      routeLongName <- c.optionally[String]("route_long_name")
      routeDesc <- c.optionally[String]("route_desc")
      routeType <- c.required[RouteType]("route_type")
      routeUrl <- c.optionally[java.net.URI]("route_url")
      routeColor <- c.optionally[Color]("route_color")
      routeTextColor <- c.optionally[Color]("route_text_color")
      routeSortOrder <- c.optionally[Int]("route_sort_order")
    } yield Routes.apply(
      routeId,
      agencyId,
      routeShortName,
      routeLongName,
      routeDesc,
      routeType,
      routeUrl,
      routeColor,
      routeTextColor,
      routeSortOrder
    )
  val Fields: Seq[String] = Seq(
    "route_id",
    "agency_id",
    "route_short_name",
    "route_long_name",
    "route_desc",
    "route_type",
    "route_url",
    "route_color",
    "route_text_color",
    "route_sort_order"
  )
}
object Trips {
  implicit val csvReader: CsvReader[Trips] = (c: CsvCursor) =>
    for {
      routeId <- c.required[RouteId]("route_id")
      serviceId <- c.required[ServiceId]("service_id")
      tripId <- c.required[TripId]("trip_id")
      tripHeadsign <- c.optionally[String]("trip_headsign")
      tripShortName <- c.optionally[String]("trip_short_name")
      directionId <- c.optionally[DirectionId]("direction_id")
      blockId <- c.optionally[BlockId]("block_id")
      shapeId <- c.optionally[ShapeId]("shape_id")
      wheelchairAccessible <- c.optionally[WheelchairAccessible]("wheelchair_accessible")
      bikesAllowed <- c.optionally[BikesAllowed]("bikes_allowed")
    } yield Trips.apply(
      routeId,
      serviceId,
      tripId,
      tripHeadsign,
      tripShortName,
      directionId,
      blockId,
      shapeId,
      wheelchairAccessible,
      bikesAllowed
    )
  val Fields: Seq[String] = Seq(
    "route_id",
    "service_id",
    "trip_id",
    "trip_headsign",
    "trip_short_name",
    "direction_id",
    "block_id",
    "shape_id",
    "wheelchair_accessible",
    "bikes_allowed"
  )
}
object StopTimes {
  implicit val csvReader: CsvReader[StopTimes] = (c: CsvCursor) =>
    for {
      tripId <- c.required[TripId]("trip_id")
      arrivalTime <- c.optionally[java.time.LocalTime]("arrival_time")
      departureTime <- c.optionally[java.time.LocalTime]("departure_time")
      stopId <- c.required[StopId]("stop_id")
      stopSequence <- c.required[Int]("stop_sequence")
      stopHeadsign <- c.optionally[String]("stop_headsign")
      pickupType <- c.optionally[PickupType]("pickup_type")
      dropOffType <- c.optionally[DropOffType]("drop_off_type")
      shapeDistTraveled <- c.optionally[Double]("shape_dist_traveled")
      timepoint <- c.optionally[Timepoint]("timepoint")
    } yield StopTimes.apply(
      tripId,
      arrivalTime,
      departureTime,
      stopId,
      stopSequence,
      stopHeadsign,
      pickupType,
      dropOffType,
      shapeDistTraveled,
      timepoint
    )
  val Fields: Seq[String] = Seq(
    "trip_id",
    "arrival_time",
    "departure_time",
    "stop_id",
    "stop_sequence",
    "stop_headsign",
    "pickup_type",
    "drop_off_type",
    "shape_dist_traveled",
    "timepoint"
  )
}
object Calendar {
  implicit val csvReader: CsvReader[Calendar] = (c: CsvCursor) =>
    for {
      serviceId <- c.required[ServiceId]("service_id")
      monday <- c.required[Boolean]("monday")
      tuesday <- c.required[Boolean]("tuesday")
      wednesday <- c.required[Boolean]("wednesday")
      thursday <- c.required[Boolean]("thursday")
      friday <- c.required[Boolean]("friday")
      saturday <- c.required[Boolean]("saturday")
      sunday <- c.required[Boolean]("sunday")
      startDate <- c.required[java.time.LocalDate]("start_date")
      endDate <- c.required[java.time.LocalDate]("end_date")
    } yield Calendar.apply(
      serviceId,
      monday,
      tuesday,
      wednesday,
      thursday,
      friday,
      saturday,
      sunday,
      startDate,
      endDate
    )
  val Fields: Seq[String] = Seq(
    "service_id",
    "monday",
    "tuesday",
    "wednesday",
    "thursday",
    "friday",
    "saturday",
    "sunday",
    "start_date",
    "end_date"
  )
}
object CalendarDates {
  implicit val csvReader: CsvReader[CalendarDates] = (c: CsvCursor) =>
    for {
      serviceId <- c.required[ServiceId]("service_id")
      date <- c.required[java.time.LocalDate]("date")
      exceptionType <- c.required[ExceptionType]("exception_type")
    } yield CalendarDates.apply(serviceId, date, exceptionType)
  val Fields: Seq[String] = Seq("service_id", "date", "exception_type")
}
object FareAttributes {
  implicit val csvReader: CsvReader[FareAttributes] = (c: CsvCursor) =>
    for {
      fareId <- c.required[FareId]("fare_id")
      price <- c.required[Double]("price")
      currencyType <- c.required[CurrencyCode]("currency_type")
      paymentMethod <- c.required[PaymentMethod]("payment_method")
      transfers <- c.required[String]("transfers")
      agencyId <- c.optionally[AgencyId]("agency_id")
      transferDuration <- c.optionally[Int]("transfer_duration")
    } yield FareAttributes.apply(
      fareId,
      price,
      currencyType,
      paymentMethod,
      transfers,
      agencyId,
      transferDuration
    )
  val Fields: Seq[String] = Seq(
    "fare_id",
    "price",
    "currency_type",
    "payment_method",
    "transfers",
    "agency_id",
    "transfer_duration"
  )
}
object FareRules {
  implicit val csvReader: CsvReader[FareRules] = (c: CsvCursor) =>
    for {
      fareId <- c.required[FareId]("fare_id")
      routeId <- c.optionally[RouteId]("route_id")
      originId <- c.optionally[ZoneId]("origin_id")
      destinationId <- c.optionally[ZoneId]("destination_id")
      containsId <- c.optionally[ZoneId]("contains_id")
    } yield FareRules.apply(fareId, routeId, originId, destinationId, containsId)
  val Fields: Seq[String] = Seq("fare_id", "route_id", "origin_id", "destination_id", "contains_id")
}
object Shapes {
  implicit val csvReader: CsvReader[Shapes] = (c: CsvCursor) =>
    for {
      shapeId <- c.required[ShapeId]("shape_id")
      shapePtLat <- c.required[Latitude]("shape_pt_lat")
      shapePtLon <- c.required[Longitude]("shape_pt_lon")
      shapePtSequence <- c.required[Int]("shape_pt_sequence")
      shapeDistTraveled <- c.optionally[Double]("shape_dist_traveled")
    } yield Shapes.apply(shapeId, shapePtLat, shapePtLon, shapePtSequence, shapeDistTraveled)
  val Fields: Seq[String] =
    Seq("shape_id", "shape_pt_lat", "shape_pt_lon", "shape_pt_sequence", "shape_dist_traveled")
}
object Frequencies {
  implicit val csvReader: CsvReader[Frequencies] = (c: CsvCursor) =>
    for {
      tripId <- c.required[TripId]("trip_id")
      startTime <- c.required[java.time.LocalTime]("start_time")
      endTime <- c.required[java.time.LocalTime]("end_time")
      headwaySecs <- c.required[Int]("headway_secs")
      exactTimes <- c.optionally[ExactTimes]("exact_times")
    } yield Frequencies.apply(tripId, startTime, endTime, headwaySecs, exactTimes)
  val Fields: Seq[String] = Seq("trip_id", "start_time", "end_time", "headway_secs", "exact_times")
}
object Transfers {
  implicit val csvReader: CsvReader[Transfers] = (c: CsvCursor) =>
    for {
      fromStopId <- c.required[StopId]("from_stop_id")
      toStopId <- c.required[StopId]("to_stop_id")
      transferType <- c.required[TransferType]("transfer_type")
      minTransferTime <- c.optionally[Int]("min_transfer_time")
    } yield Transfers.apply(fromStopId, toStopId, transferType, minTransferTime)
  val Fields: Seq[String] = Seq("from_stop_id", "to_stop_id", "transfer_type", "min_transfer_time")
}
object Pathways {
  implicit val csvReader: CsvReader[Pathways] = (c: CsvCursor) =>
    for {
      pathwayId <- c.required[PathwayId]("pathway_id")
      fromStopId <- c.required[StopId]("from_stop_id")
      toStopId <- c.required[StopId]("to_stop_id")
      pathwayMode <- c.required[PathwayMode]("pathway_mode")
      isBidirectional <- c.required[Boolean]("is_bidirectional")
      length <- c.optionally[Double]("length")
      traversalTime <- c.optionally[Int]("traversal_time")
      stairCount <- c.optionally[Int]("stair_count")
      maxSlope <- c.optionally[Double]("max_slope")
      minWidth <- c.optionally[Double]("min_width")
      signpostedAs <- c.optionally[String]("signposted_as")
      reversedSignpostedAs <- c.optionally[String]("reversed_signposted_as")
    } yield Pathways.apply(
      pathwayId,
      fromStopId,
      toStopId,
      pathwayMode,
      isBidirectional,
      length,
      traversalTime,
      stairCount,
      maxSlope,
      minWidth,
      signpostedAs,
      reversedSignpostedAs
    )
  val Fields: Seq[String] = Seq(
    "pathway_id",
    "from_stop_id",
    "to_stop_id",
    "pathway_mode",
    "is_bidirectional",
    "length",
    "traversal_time",
    "stair_count",
    "max_slope",
    "min_width",
    "signposted_as",
    "reversed_signposted_as"
  )
}
object Levels {
  implicit val csvReader: CsvReader[Levels] = (c: CsvCursor) =>
    for {
      levelId <- c.required[LevelId]("level_id")
      levelIndex <- c.required[Double]("level_index")
      levelName <- c.optionally[String]("level_name")
    } yield Levels.apply(levelId, levelIndex, levelName)
  val Fields: Seq[String] = Seq("level_id", "level_index", "level_name")
}
object FeedInfo {
  implicit val csvReader: CsvReader[FeedInfo] = (c: CsvCursor) =>
    for {
      feedPublisherName <- c.required[String]("feed_publisher_name")
      feedPublisherUrl <- c.required[java.net.URI]("feed_publisher_url")
      feedLang <- c.required[LanguageCode]("feed_lang")
      defaultLang <- c.optionally[LanguageCode]("default_lang")
      feedStartDate <- c.optionally[java.time.LocalDate]("feed_start_date")
      feedEndDate <- c.optionally[java.time.LocalDate]("feed_end_date")
      feedVersion <- c.optionally[String]("feed_version")
      feedContactEmail <- c.optionally[String]("feed_contact_email")
      feedContactUrl <- c.optionally[java.net.URI]("feed_contact_url")
    } yield FeedInfo.apply(
      feedPublisherName,
      feedPublisherUrl,
      feedLang,
      defaultLang,
      feedStartDate,
      feedEndDate,
      feedVersion,
      feedContactEmail,
      feedContactUrl
    )
  val Fields: Seq[String] = Seq(
    "feed_publisher_name",
    "feed_publisher_url",
    "feed_lang",
    "default_lang",
    "feed_start_date",
    "feed_end_date",
    "feed_version",
    "feed_contact_email",
    "feed_contact_url"
  )
}
object Translations {
  implicit val csvReader: CsvReader[Translations] = (c: CsvCursor) =>
    for {
      tableName <- c.required[TableName]("table_name")
      fieldName <- c.required[String]("field_name")
      language <- c.required[LanguageCode]("language")
      translation <- c.required[String]("translation")
      recordId <- c.optionally[RecordId]("record_id")
      recordSubId <- c.optionally[RecordSubId]("record_sub_id")
      fieldValue <- c.optionally[String]("field_value")
    } yield Translations.apply(
      tableName,
      fieldName,
      language,
      translation,
      recordId,
      recordSubId,
      fieldValue
    )
  val Fields: Seq[String] = Seq(
    "table_name",
    "field_name",
    "language",
    "translation",
    "record_id",
    "record_sub_id",
    "field_value"
  )
}
object Attributions {
  implicit val csvReader: CsvReader[Attributions] = (c: CsvCursor) =>
    for {
      attributionId <- c.optionally[AttributionId]("attribution_id")
      agencyId <- c.optionally[AgencyId]("agency_id")
      routeId <- c.optionally[RouteId]("route_id")
      tripId <- c.optionally[TripId]("trip_id")
      organizationName <- c.required[String]("organization_name")
      isProducer <- c.optionally[Boolean]("is_producer")
      isOperator <- c.optionally[Boolean]("is_operator")
      isAuthority <- c.optionally[Boolean]("is_authority")
      attributionUrl <- c.optionally[java.net.URI]("attribution_url")
      attributionEmail <- c.optionally[String]("attribution_email")
      attributionPhone <- c.optionally[String]("attribution_phone")
    } yield Attributions.apply(
      attributionId,
      agencyId,
      routeId,
      tripId,
      organizationName,
      isProducer,
      isOperator,
      isAuthority,
      attributionUrl,
      attributionEmail,
      attributionPhone
    )
  val Fields: Seq[String] = Seq(
    "attribution_id",
    "agency_id",
    "route_id",
    "trip_id",
    "organization_name",
    "is_producer",
    "is_operator",
    "is_authority",
    "attribution_url",
    "attribution_email",
    "attribution_phone"
  )
}
