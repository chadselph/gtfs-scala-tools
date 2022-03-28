package me.chadrs.gtfstools.graphql

import java.util.concurrent.TimeUnit
import com.github.benmanes.caffeine.cache.{LoadingCache, Caffeine}
import me.chadrs.gtfstools.csv.CsvRowViewer
import me.chadrs.gtfstools.parsing.GtfsInput
import me.chadrs.gtfstools.types.{RoutesFileRow, RouteId, TripsFileRow, TripId}
import sangria.schema._

object Schemas {

  trait GtfsContext {
    def filterFileBy[T: CsvRowViewer](
        path: String,
        filterCol: String,
        filterValue: String
    ): Either[String, IndexedSeq[T]]

    def filterFileBy[T: CsvRowViewer](
        path: String,
        predicate: T => Boolean
    ): Either[String, IndexedSeq[T]]
  }

  class GtfsRepo(zipPath: String) extends GtfsContext {
    val zipFile =
      GtfsInput.fromString(zipPath).getOrElse(throw new Exception("Invalid url")).toGtfsZipFile

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

    override def filterFileBy[T: CsvRowViewer](
        path: String,
        filterCol: String,
        filterValue: String
    ): Either[String, IndexedSeq[T]] = {
      zipFile.parseFilteredFile[T](path, filterCol, filterValue)
    }

    override def filterFileBy[T: CsvRowViewer](
        path: String,
        predicate: T => Boolean
    ): Either[String, IndexedSeq[T]] = {
      zipFile.parseFile[T](path).map(_.filter(predicate))
    }
  }

  object GtfsCache {

    def logTime[T](s: String)(body: => T): T = {
      val start = System.currentTimeMillis()
      val result = body
      println(s"Took ${System.currentTimeMillis() - start}ms to load $s")
      result
    }

    def init(): GtfsCache = {
      val cache: LoadingCache[String, GtfsRepo] = Caffeine
        .newBuilder()
        .maximumSize(10)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build(url => {
          logTime(url) {
            new GtfsRepo(url)
          }
        })
      GtfsCache(cache, None)
    }
  }

  case class GtfsCache(cache: LoadingCache[String, GtfsRepo], active: Option[String])
      extends GtfsContext {

    private val activeFeed = active.toRight("No active url!").map(forUrl)

    def forUrl(s: String): GtfsRepo = cache.get(s)

    override def filterFileBy[T: CsvRowViewer](
        path: String,
        filterCol: String,
        filterValue: String
    ): Either[String, IndexedSeq[T]] =
      activeFeed.flatMap(_.filterFileBy(path, filterCol, filterValue))

    override def filterFileBy[T: CsvRowViewer](
        path: String,
        predicate: T => Boolean
    ): Either[String, IndexedSeq[T]] =
      activeFeed.flatMap(_.filterFileBy(path, predicate))
  }

  val Id = Argument("id", StringType)
  val gtfsUrl = Argument("url", StringType)
  val FeedType: ObjectType[GtfsCache, GtfsRepo] = ObjectType(
    "Feed",
    fields[GtfsCache, GtfsRepo](
      Field(
        "routes",
        ListType(GeneratedSchemas.routesSchema),
        resolve = _.value.parseFile[RoutesFileRow]("routes.txt").getOrElse(Nil)
      ),
      Field(
        "route",
        OptionType(GeneratedSchemas.routesSchema),
        Some("Select a specific route by id or short name"),
        List(Id),
        resolve = c => c.value.getRoute(c.arg(Id))
      ),
      Field(
        "trip",
        OptionType(GeneratedSchemas.tripsSchema),
        Some("Select a specfic trip by id or trip short name"),
        List(Id),
        resolve = c => c.value.getTrip(c.arg(Id))
      )
    )
  )
  val QueryFile: ObjectType[GtfsCache, Any] = ObjectType(
    "Query",
    fields[GtfsCache, Any](
      Field(
        "feed",
        FeedType,
        Some("Query a GTFS feed by URL"),
        List(gtfsUrl),
        resolve = c =>
          UpdateCtx(c.ctx.forUrl(c.arg(gtfsUrl))) { _ =>
            c.ctx.copy(active = Some(c.arg(gtfsUrl)))
          }
      )
    )
  )
  val schema = Schema(QueryFile)
  /*
   * TODO: types. everything is Option[String]/List[String]
   * TODO: link service/calendar/calendar_days from trips
   */

}
