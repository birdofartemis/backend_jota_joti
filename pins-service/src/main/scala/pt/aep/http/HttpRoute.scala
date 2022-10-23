package pt.aep.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import pt.aep.http.routes.{PersonRoute, PinRoute}
import pt.aep.services.{PersonServiceImpl, PinServiceImpl}
import akka.http.scaladsl.model.HttpMethods._
import scala.collection.immutable

import scala.concurrent.ExecutionContext

class HttpRoute(personService: PersonServiceImpl, pinService: PinServiceImpl)(implicit ec: ExecutionContext) {
  private val personRoute = new PersonRoute(personService)
  private val pinRoute = new PinRoute(pinService)
  private val settings = CorsSettings.defaultSettings.withAllowedMethods(immutable.Seq(
    GET, PUT, POST, DELETE, HEAD, OPTIONS
  ))

  val route: Route =
    cors(settings) {
      pathPrefix("api") {
        personRoute.route ~
        pinRoute.route
      } ~
        pathPrefix("healthcheck") {
          get {
            complete("Ok")
          }
        }
    }
}
