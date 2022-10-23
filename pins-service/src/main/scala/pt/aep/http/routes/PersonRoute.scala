package pt.aep.http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import pt.aep.domain.entities.Person
import pt.aep.services.PersonServiceImpl

import scala.concurrent.ExecutionContext

class PersonRoute(personService: PersonServiceImpl)(implicit ec: ExecutionContext) extends FailFastCirceSupport {
  import StatusCodes._
  import personService._

  val route = pathPrefix("person") {
    pathEndOrSingleSlash {
      post {
        entity(as[Person]) { person =>
          complete(createPerson(person).map(_.asJson))
        }
      }
    }
  } ~
    pathPrefix("person") {
      pathPrefix(Segment) { id =>
        pathEndOrSingleSlash {
          get {
            complete(findById(id.toInt).map {
              case Some(person) => OK -> person.asJson
              case None => BadRequest -> s"Couldn't find scout with id: $id.".asJson
            })
          }
        }
      }
    }

}
