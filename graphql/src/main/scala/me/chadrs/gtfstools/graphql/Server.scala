package me.chadrs.gtfstools.graphql
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.{Decoder, Json}
import me.chadrs.gtfstools.graphql.Schemas.GtfsCache
import sangria.execution.{Executor, ValidationError}
import sangria.marshalling.circe._
import sangria.parser.{QueryParser, SyntaxError}

import scala.concurrent.Future

case class GraphQLRequest(query: String, operationName: Option[String], varOpt: Option[Json]) {
  def variables: Json = varOpt.getOrElse(Json.obj())
}

object GraphQLRequest {
  implicit val decodeReq: Decoder[GraphQLRequest] =
    Decoder.forProduct3("query", "operationName", "variables")(GraphQLRequest.apply)
}

object Server {

  private val gtfsCache = GtfsCache.init()

  implicit val system: ActorSystem = ActorSystem("sangria-server")
  import system.dispatcher

  implicit def excHandler: ExceptionHandler =
    ExceptionHandler {
      case e: ValidationError => complete((400, e.getMessage()))
      case e: SyntaxError     => complete((400, e.getMessage()))
    }

  def main(args: Array[String]): Unit = {
    val port = sys.env.getOrElse("PORT", "5000").toInt
    println(s"Starting up on port $port")
    val route: Route = Route.seal((post & path("graphql")) {
      entity(as[GraphQLRequest]) { requestJson ⇒
        complete(graphQLEndpoint(requestJson))
      }
    })

    Http().newServerAt("0.0.0.0", port).bindFlow(route)
  }

  def graphQLEndpoint(body: GraphQLRequest): Future[Json] = {
    Future.fromTry(QueryParser.parse(body.query)).flatMap { value =>
      Executor
        .execute(
          Schemas.schema,
          value,
          gtfsCache,
          variables = body.variables,
          operationName = body.operationName
        )
    }

  }

}
