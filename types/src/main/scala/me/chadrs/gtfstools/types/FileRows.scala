package me.chadrs.gtfstools.types

import me.chadrs.gtfstools.csv._

class AgencyFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val agencyId: Either[String, Option[AgencyId]] = cursor.optionally[AgencyId]("agency_id")
  lazy val agencyName: Either[String, String] = cursor.required[String]("agency_name")
  lazy val agencyUrl: Either[String, java.net.URI] = cursor.required[java.net.URI]("agency_url")
  lazy val agencyTimezone: Either[String, java.time.ZoneId] =
    cursor.required[java.time.ZoneId]("agency_timezone")
  lazy val agencyLang: Either[String, Option[LanguageCode]] =
    cursor.optionally[LanguageCode]("agency_lang")
  lazy val agencyPhone: Either[String, Option[String]] = cursor.optionally[String]("agency_phone")
  lazy val agencyFareUrl: Either[String, Option[java.net.URI]] =
    cursor.optionally[java.net.URI]("agency_fare_url")
  lazy val agencyEmail: Either[String, Option[String]] = cursor.optionally[String]("agency_email")
}
class StopsFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val stopId: Either[String, StopId] = cursor.required[StopId]("stop_id")
  lazy val stopCode: Either[String, Option[String]] = cursor.optionally[String]("stop_code")
  lazy val stopName: Either[String, Option[String]] = cursor.optionally[String]("stop_name")
  lazy val stopDesc: Either[String, Option[String]] = cursor.optionally[String]("stop_desc")
  lazy val stopLat: Either[String, Option[Latitude]] = cursor.optionally[Latitude]("stop_lat")
  lazy val stopLon: Either[String, Option[Longitude]] = cursor.optionally[Longitude]("stop_lon")
  lazy val zoneId: Either[String, Option[ZoneId]] = cursor.optionally[ZoneId]("zone_id")
  lazy val stopUrl: Either[String, Option[java.net.URI]] =
    cursor.optionally[java.net.URI]("stop_url")
  lazy val locationType: Either[String, Option[LocationType]] =
    cursor.optionally[LocationType]("location_type")
  lazy val parentStation: Either[String, Option[StopId]] =
    cursor.optionally[StopId]("parent_station")
  lazy val stopTimezone: Either[String, Option[java.time.ZoneId]] =
    cursor.optionally[java.time.ZoneId]("stop_timezone")
  lazy val wheelchairBoarding: Either[String, Option[WheelchairBoarding]] =
    cursor.optionally[WheelchairBoarding]("wheelchair_boarding")
  lazy val levelId: Either[String, Option[LevelId]] = cursor.optionally[LevelId]("level_id")
  lazy val platformCode: Either[String, Option[String]] = cursor.optionally[String]("platform_code")
}
class RoutesFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val routeId: Either[String, RouteId] = cursor.required[RouteId]("route_id")
  lazy val agencyId: Either[String, Option[AgencyId]] = cursor.optionally[AgencyId]("agency_id")
  lazy val routeShortName: Either[String, Option[String]] =
    cursor.optionally[String]("route_short_name")
  lazy val routeLongName: Either[String, Option[String]] =
    cursor.optionally[String]("route_long_name")
  lazy val routeDesc: Either[String, Option[String]] = cursor.optionally[String]("route_desc")
  lazy val routeType: Either[String, RouteType] = cursor.required[RouteType]("route_type")
  lazy val routeUrl: Either[String, Option[java.net.URI]] =
    cursor.optionally[java.net.URI]("route_url")
  lazy val routeColor: Either[String, Option[Color]] = cursor.optionally[Color]("route_color")
  lazy val routeTextColor: Either[String, Option[Color]] =
    cursor.optionally[Color]("route_text_color")
  lazy val routeSortOrder: Either[String, Option[Int]] = cursor.optionally[Int]("route_sort_order")
}
class TripsFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val routeId: Either[String, RouteId] = cursor.required[RouteId]("route_id")
  lazy val serviceId: Either[String, ServiceId] = cursor.required[ServiceId]("service_id")
  lazy val tripId: Either[String, TripId] = cursor.required[TripId]("trip_id")
  lazy val tripHeadsign: Either[String, Option[String]] = cursor.optionally[String]("trip_headsign")
  lazy val tripShortName: Either[String, Option[String]] =
    cursor.optionally[String]("trip_short_name")
  lazy val directionId: Either[String, Option[DirectionId]] =
    cursor.optionally[DirectionId]("direction_id")
  lazy val blockId: Either[String, Option[BlockId]] = cursor.optionally[BlockId]("block_id")
  lazy val shapeId: Either[String, Option[ShapeId]] = cursor.optionally[ShapeId]("shape_id")
  lazy val wheelchairAccessible: Either[String, Option[WheelchairAccessible]] =
    cursor.optionally[WheelchairAccessible]("wheelchair_accessible")
  lazy val bikesAllowed: Either[String, Option[BikesAllowed]] =
    cursor.optionally[BikesAllowed]("bikes_allowed")
}
class StopTimesFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val tripId: Either[String, TripId] = cursor.required[TripId]("trip_id")
  lazy val arrivalTime: Either[String, Option[java.time.LocalTime]] =
    cursor.optionally[java.time.LocalTime]("arrival_time")
  lazy val departureTime: Either[String, Option[java.time.LocalTime]] =
    cursor.optionally[java.time.LocalTime]("departure_time")
  lazy val stopId: Either[String, StopId] = cursor.required[StopId]("stop_id")
  lazy val stopSequence: Either[String, Int] = cursor.required[Int]("stop_sequence")
  lazy val stopHeadsign: Either[String, Option[String]] = cursor.optionally[String]("stop_headsign")
  lazy val pickupType: Either[String, Option[PickupType]] =
    cursor.optionally[PickupType]("pickup_type")
  lazy val dropOffType: Either[String, Option[DropOffType]] =
    cursor.optionally[DropOffType]("drop_off_type")
  lazy val shapeDistTraveled: Either[String, Option[Double]] =
    cursor.optionally[Double]("shape_dist_traveled")
  lazy val timepoint: Either[String, Option[Timepoint]] = cursor.optionally[Timepoint]("timepoint")
}
class CalendarFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val serviceId: Either[String, ServiceId] = cursor.required[ServiceId]("service_id")
  lazy val monday: Either[String, Boolean] = cursor.required[Boolean]("monday")
  lazy val tuesday: Either[String, Boolean] = cursor.required[Boolean]("tuesday")
  lazy val wednesday: Either[String, Boolean] = cursor.required[Boolean]("wednesday")
  lazy val thursday: Either[String, Boolean] = cursor.required[Boolean]("thursday")
  lazy val friday: Either[String, Boolean] = cursor.required[Boolean]("friday")
  lazy val saturday: Either[String, Boolean] = cursor.required[Boolean]("saturday")
  lazy val sunday: Either[String, Boolean] = cursor.required[Boolean]("sunday")
  lazy val startDate: Either[String, java.time.LocalDate] =
    cursor.required[java.time.LocalDate]("start_date")
  lazy val endDate: Either[String, java.time.LocalDate] =
    cursor.required[java.time.LocalDate]("end_date")
}
class CalendarDatesFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val serviceId: Either[String, ServiceId] = cursor.required[ServiceId]("service_id")
  lazy val date: Either[String, java.time.LocalDate] = cursor.required[java.time.LocalDate]("date")
  lazy val exceptionType: Either[String, ExceptionType] =
    cursor.required[ExceptionType]("exception_type")
}
class FareAttributesFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val fareId: Either[String, FareId] = cursor.required[FareId]("fare_id")
  lazy val price: Either[String, Double] = cursor.required[Double]("price")
  lazy val currencyType: Either[String, CurrencyCode] =
    cursor.required[CurrencyCode]("currency_type")
  lazy val paymentMethod: Either[String, PaymentMethod] =
    cursor.required[PaymentMethod]("payment_method")
  lazy val transfers: Either[String, String] = cursor.required[String]("transfers")
  lazy val agencyId: Either[String, Option[AgencyId]] = cursor.optionally[AgencyId]("agency_id")
  lazy val transferDuration: Either[String, Option[Int]] =
    cursor.optionally[Int]("transfer_duration")
}
class FareRulesFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val fareId: Either[String, FareId] = cursor.required[FareId]("fare_id")
  lazy val routeId: Either[String, Option[RouteId]] = cursor.optionally[RouteId]("route_id")
  lazy val originId: Either[String, Option[ZoneId]] = cursor.optionally[ZoneId]("origin_id")
  lazy val destinationId: Either[String, Option[ZoneId]] =
    cursor.optionally[ZoneId]("destination_id")
  lazy val containsId: Either[String, Option[ZoneId]] = cursor.optionally[ZoneId]("contains_id")
}
class ShapesFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val shapeId: Either[String, ShapeId] = cursor.required[ShapeId]("shape_id")
  lazy val shapePtLat: Either[String, Latitude] = cursor.required[Latitude]("shape_pt_lat")
  lazy val shapePtLon: Either[String, Longitude] = cursor.required[Longitude]("shape_pt_lon")
  lazy val shapePtSequence: Either[String, Int] = cursor.required[Int]("shape_pt_sequence")
  lazy val shapeDistTraveled: Either[String, Option[Double]] =
    cursor.optionally[Double]("shape_dist_traveled")
}
class FrequenciesFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val tripId: Either[String, TripId] = cursor.required[TripId]("trip_id")
  lazy val startTime: Either[String, java.time.LocalTime] =
    cursor.required[java.time.LocalTime]("start_time")
  lazy val endTime: Either[String, java.time.LocalTime] =
    cursor.required[java.time.LocalTime]("end_time")
  lazy val headwaySecs: Either[String, Int] = cursor.required[Int]("headway_secs")
  lazy val exactTimes: Either[String, Option[ExactTimes]] =
    cursor.optionally[ExactTimes]("exact_times")
}
class TransfersFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val fromStopId: Either[String, StopId] = cursor.required[StopId]("from_stop_id")
  lazy val toStopId: Either[String, StopId] = cursor.required[StopId]("to_stop_id")
  lazy val transferType: Either[String, TransferType] =
    cursor.required[TransferType]("transfer_type")
  lazy val minTransferTime: Either[String, Option[Int]] =
    cursor.optionally[Int]("min_transfer_time")
}
class PathwaysFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val pathwayId: Either[String, PathwayId] = cursor.required[PathwayId]("pathway_id")
  lazy val fromStopId: Either[String, StopId] = cursor.required[StopId]("from_stop_id")
  lazy val toStopId: Either[String, StopId] = cursor.required[StopId]("to_stop_id")
  lazy val pathwayMode: Either[String, PathwayMode] = cursor.required[PathwayMode]("pathway_mode")
  lazy val isBidirectional: Either[String, Boolean] = cursor.required[Boolean]("is_bidirectional")
  lazy val length: Either[String, Option[Double]] = cursor.optionally[Double]("length")
  lazy val traversalTime: Either[String, Option[Int]] = cursor.optionally[Int]("traversal_time")
  lazy val stairCount: Either[String, Option[Int]] = cursor.optionally[Int]("stair_count")
  lazy val maxSlope: Either[String, Option[Double]] = cursor.optionally[Double]("max_slope")
  lazy val minWidth: Either[String, Option[Double]] = cursor.optionally[Double]("min_width")
  lazy val signpostedAs: Either[String, Option[String]] = cursor.optionally[String]("signposted_as")
  lazy val reversedSignpostedAs: Either[String, Option[String]] =
    cursor.optionally[String]("reversed_signposted_as")
}
class LevelsFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val levelId: Either[String, LevelId] = cursor.required[LevelId]("level_id")
  lazy val levelIndex: Either[String, Double] = cursor.required[Double]("level_index")
  lazy val levelName: Either[String, Option[String]] = cursor.optionally[String]("level_name")
}
class FeedInfoFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val feedPublisherName: Either[String, String] =
    cursor.required[String]("feed_publisher_name")
  lazy val feedPublisherUrl: Either[String, java.net.URI] =
    cursor.required[java.net.URI]("feed_publisher_url")
  lazy val feedLang: Either[String, LanguageCode] = cursor.required[LanguageCode]("feed_lang")
  lazy val defaultLang: Either[String, Option[LanguageCode]] =
    cursor.optionally[LanguageCode]("default_lang")
  lazy val feedStartDate: Either[String, Option[java.time.LocalDate]] =
    cursor.optionally[java.time.LocalDate]("feed_start_date")
  lazy val feedEndDate: Either[String, Option[java.time.LocalDate]] =
    cursor.optionally[java.time.LocalDate]("feed_end_date")
  lazy val feedVersion: Either[String, Option[String]] = cursor.optionally[String]("feed_version")
  lazy val feedContactEmail: Either[String, Option[String]] =
    cursor.optionally[String]("feed_contact_email")
  lazy val feedContactUrl: Either[String, Option[java.net.URI]] =
    cursor.optionally[java.net.URI]("feed_contact_url")
}
class TranslationsFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val tableName: Either[String, TableName] = cursor.required[TableName]("table_name")
  lazy val fieldName: Either[String, String] = cursor.required[String]("field_name")
  lazy val language: Either[String, LanguageCode] = cursor.required[LanguageCode]("language")
  lazy val translation: Either[String, String] = cursor.required[String]("translation")
  lazy val recordId: Either[String, Option[RecordId]] = cursor.optionally[RecordId]("record_id")
  lazy val recordSubId: Either[String, Option[RecordSubId]] =
    cursor.optionally[RecordSubId]("record_sub_id")
  lazy val fieldValue: Either[String, Option[String]] = cursor.optionally[String]("field_value")
}
class AttributionsFileRow(cursor: me.chadrs.gtfstools.csv.CsvCursor) {
  lazy val toMap: Map[String, String] = cursor.toMap
  def get(field: String): Option[String] = toMap.get(field)
  lazy val attributionId: Either[String, Option[AttributionId]] =
    cursor.optionally[AttributionId]("attribution_id")
  lazy val agencyId: Either[String, Option[AgencyId]] = cursor.optionally[AgencyId]("agency_id")
  lazy val routeId: Either[String, Option[RouteId]] = cursor.optionally[RouteId]("route_id")
  lazy val tripId: Either[String, Option[TripId]] = cursor.optionally[TripId]("trip_id")
  lazy val organizationName: Either[String, String] = cursor.required[String]("organization_name")
  lazy val isProducer: Either[String, Option[Boolean]] = cursor.optionally[Boolean]("is_producer")
  lazy val isOperator: Either[String, Option[Boolean]] = cursor.optionally[Boolean]("is_operator")
  lazy val isAuthority: Either[String, Option[Boolean]] = cursor.optionally[Boolean]("is_authority")
  lazy val attributionUrl: Either[String, Option[java.net.URI]] =
    cursor.optionally[java.net.URI]("attribution_url")
  lazy val attributionEmail: Either[String, Option[String]] =
    cursor.optionally[String]("attribution_email")
  lazy val attributionPhone: Either[String, Option[String]] =
    cursor.optionally[String]("attribution_phone")
}
object AgencyFileRow {
  implicit val csvReader: CsvRowViewer[AgencyFileRow] = (cursor: CsvCursor) =>
    new AgencyFileRow(cursor)
}
object StopsFileRow {
  implicit val csvReader: CsvRowViewer[StopsFileRow] = (cursor: CsvCursor) =>
    new StopsFileRow(cursor)
}
object RoutesFileRow {
  implicit val csvReader: CsvRowViewer[RoutesFileRow] = (cursor: CsvCursor) =>
    new RoutesFileRow(cursor)
}
object TripsFileRow {
  implicit val csvReader: CsvRowViewer[TripsFileRow] = (cursor: CsvCursor) =>
    new TripsFileRow(cursor)
}
object StopTimesFileRow {
  implicit val csvReader: CsvRowViewer[StopTimesFileRow] = (cursor: CsvCursor) =>
    new StopTimesFileRow(cursor)
}
object CalendarFileRow {
  implicit val csvReader: CsvRowViewer[CalendarFileRow] = (cursor: CsvCursor) =>
    new CalendarFileRow(cursor)
}
object CalendarDatesFileRow {
  implicit val csvReader: CsvRowViewer[CalendarDatesFileRow] = (cursor: CsvCursor) =>
    new CalendarDatesFileRow(cursor)
}
object FareAttributesFileRow {
  implicit val csvReader: CsvRowViewer[FareAttributesFileRow] = (cursor: CsvCursor) =>
    new FareAttributesFileRow(cursor)
}
object FareRulesFileRow {
  implicit val csvReader: CsvRowViewer[FareRulesFileRow] = (cursor: CsvCursor) =>
    new FareRulesFileRow(cursor)
}
object ShapesFileRow {
  implicit val csvReader: CsvRowViewer[ShapesFileRow] = (cursor: CsvCursor) =>
    new ShapesFileRow(cursor)
}
object FrequenciesFileRow {
  implicit val csvReader: CsvRowViewer[FrequenciesFileRow] = (cursor: CsvCursor) =>
    new FrequenciesFileRow(cursor)
}
object TransfersFileRow {
  implicit val csvReader: CsvRowViewer[TransfersFileRow] = (cursor: CsvCursor) =>
    new TransfersFileRow(cursor)
}
object PathwaysFileRow {
  implicit val csvReader: CsvRowViewer[PathwaysFileRow] = (cursor: CsvCursor) =>
    new PathwaysFileRow(cursor)
}
object LevelsFileRow {
  implicit val csvReader: CsvRowViewer[LevelsFileRow] = (cursor: CsvCursor) =>
    new LevelsFileRow(cursor)
}
object FeedInfoFileRow {
  implicit val csvReader: CsvRowViewer[FeedInfoFileRow] = (cursor: CsvCursor) =>
    new FeedInfoFileRow(cursor)
}
object TranslationsFileRow {
  implicit val csvReader: CsvRowViewer[TranslationsFileRow] = (cursor: CsvCursor) =>
    new TranslationsFileRow(cursor)
}
object AttributionsFileRow {
  implicit val csvReader: CsvRowViewer[AttributionsFileRow] = (cursor: CsvCursor) =>
    new AttributionsFileRow(cursor)
}
