package me.chadrs.gtfstools.graphql

import java.io.FileInputStream

import cats.implicits._
import me.chadrs.gtfstools.cli.GtfsZipFile
import sangria.schema._
import sangria.macros.derive._
import sangria.execution._
import me.chadrs.gtfstools.types.{RouteId, Routes, RoutesFileRow, TripId, TripsFileRow}
import sangria.macros._
import io.circe.Json
import me.chadrs.gtfstools.csv.CsvRowViewer

import scala.concurrent.duration._
import sangria.marshalling.circe._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

object Schemas {

  // implicit val routeIdType = ScalarType[RouteId]

  val RouteType = ObjectType(
    "Route",
    "From routes.txt",
    fields[Unit, Routes](
      Field(
        "route_id",
        StringType,
        Some("The id of this route"),
        resolve = _.value.routeId.toString
      ),
      Field(
        "agency_id",
        OptionType(StringType),
        Some("agency from agencies.txt"),
        resolve = _.value.agencyId.map(_.toString)
      ),
      Field(
        "route_short_name",
        OptionType(StringType),
        Some("Short name for this route!"),
        resolve = _.value.routeShortName
      )
    )
  )

  val routeType3 = ObjectType(
    "Route",
    "from routes.txt",
    fields[Unit, RoutesFileRow](
      Field(
        "route_id",
        StringType,
        Some("The id of the route"),
        resolve = _.value.routeId.map(_.toString).leftMap(new Exception(_)).toTry
      ),
      Field(
        "route_short_name",
        OptionType(StringType),
        Some("The short name route"),
        resolve = _.value.routeShortName.leftMap(new Exception(_)).toTry
      )
    )
  )

  /*
  implicit val RouteType2 = deriveObjectType[Unit, Routes](
    ObjectTypeDescription("A route from routes.txt"),
    DocumentField("routeId", "Unique id for the route")
  )
   */
}

object Test {

  class GtfsRepo {
    val zipFile = new GtfsZipFile(new FileInputStream("gtfs.zip"))

    def parseFile[T: CsvRowViewer](f: String): Either[String, IndexedSeq[T]] =
      zipFile.parseFile[T](f)

    def getRoute(id: String): Option[RoutesFileRow] =
      parseFile[RoutesFileRow]("routes.txt").getOrElse(Nil).collectFirst {
        case row if row.routeId.contains(RouteId(id)) || row.routeShortName.contains(Some(id)) =>
          row
      }

    def getTrip(id: String): Option[TripsFileRow] =
      parseFile[TripsFileRow]("trips.txt").getOrElse(Nil).collectFirst {
        case trip if trip.tripId.contains(TripId(id)) || trip.tripShortName.contains(Some(id)) =>
          trip
      }

    def parseAndFilter[T: CsvRowViewer](path: String, filterCol: String, filterValue: String) = {
      zipFile.parseFilteredFile[T](path, filterCol, filterValue)
    }
  }

  val Id = Argument("id", StringType)
  val QueryType = ObjectType(
    "Query",
    fields[GtfsRepo, Unit](
      Field(
        "routes",
        ListType(GeneratedSchemas.routesSchema),
        resolve = _.ctx.parseFile[RoutesFileRow]("routes.txt").getOrElse(Nil)
      ),
      Field(
        "route",
        OptionType(GeneratedSchemas.routesSchema),
        Some("Select a specific route by id or short name"),
        List(Id),
        resolve = c => c.ctx.getRoute(c.arg(Id))
      ),
      Field(
        "trip",
        OptionType(GeneratedSchemas.tripsSchema),
        Some("Select a specfic trip by id or trip short name"),
        List(Id),
        resolve = c => c.ctx.getTrip(c.arg(Id))
      )
    )
  )
  val schema = Schema(QueryType)

  val query = graphql"""
    {
      routes {
        route_id
        route_short_name
        route_color
        route_url
      }
    }
  """

}
