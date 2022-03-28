package me.chadrs.gtfstools.graphql

import java.io.FileInputStream

import cats.implicits._
import me.chadrs.gtfstools.cli.GtfsZipFile
import sangria.schema._
import sangria.macros.derive._
import sangria.execution._
import me.chadrs.gtfstools.types.{RouteId, Routes, RoutesFileRow}
import sangria.macros._
import io.circe.Json

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
    val rows = new GtfsZipFile(new FileInputStream("gtfs.zip"))
      .parseFile[RoutesFileRow]("routes.txt")
  }

  val QueryType = ObjectType(
    "Query",
    fields[GtfsRepo, Unit](
      Field("routes", ListType(GeneratedSchemas.routesSchema), resolve = _.ctx.rows.getOrElse(Nil))
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
