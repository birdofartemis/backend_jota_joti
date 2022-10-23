package pt.aep.http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import pt.aep.domain.entities.Pin
import pt.aep.services.PinServiceImpl

import scala.concurrent.ExecutionContext

class PinRoute(pinService: PinServiceImpl)(implicit ec: ExecutionContext) extends FailFastCirceSupport {
  import StatusCodes._
  import pinService._

  val route = pathPrefix("pin"){
    pathEndOrSingleSlash {
      post {
        entity(as[Pin]) { pin =>
          complete(createPin(pin).map(_.asJson))
        }
      }
    } ~
      pathPrefix(Segment) { id =>
        pathEndOrSingleSlash {
          delete {
            complete(deletePin(id.toInt).map {
              case 0 => BadRequest -> false.asJson
              case _ => OK -> true.asJson
            })
          }
        }
      }
  } ~
    pathPrefix("pins") {
        pathPrefix("group") {
          pathPrefix("results"){
            get {
              complete(getResultsByGroup.map{ result =>
                OK -> result.asJson
              })
            }
          } ~
            pathPrefix(Segment) {groupNumber =>
              pathEndOrSingleSlash {
                get {
                  complete(getPinsByGroup(groupNumber.toInt).map { pins =>
                    OK -> pins.asJson
                  })
                }
              }
            }
        } ~
          pathPrefix("division") {
            pathPrefix("results"){
              get {
                complete(getResultsByDivision.map{ results =>
                  OK -> results.asJson
                })
              }
            } ~
              pathPrefix(Segment) {divisionName =>
                pathEndOrSingleSlash {
                  get {
                    complete(getPinsByDivision(divisionName).map { pins =>
                      OK -> pins.asJson
                    })
                  }
                }
              }
          }
    }
}
