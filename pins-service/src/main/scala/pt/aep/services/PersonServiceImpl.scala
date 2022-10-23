package pt.aep.services

import org.slf4j.{Logger, LoggerFactory}
import pt.aep.domain.entities.Person
import pt.aep.utils.db.{DBConnector, PersonTable}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PersonServiceImpl(dbConnector: DBConnector)(implicit ec: ExecutionContext) extends PersonService with PersonTable {

  protected val databaseConnector: DBConnector = dbConnector
  val logger: Logger = LoggerFactory.getLogger(classOf[PersonServiceImpl])

  import slick.jdbc.PostgresProfile.api._

  def createPerson(person: Person): Future[Int] = {
    val personName = s"${person.firstName} ${person.lastName}"
    val queryDescription = (persons returning persons.map(_.id)) += person

    logger.info("Trying to insert scout: {}", personName)
    dbConnector.db.run(queryDescription).andThen {
      case Failure(e) => logger.error("Error inserting scout with name: {}... Error: {}", personName, e.getMessage)
      case Success(id) => logger.info("Scout: {} was inserted with success with id: {}", personName, id)
    }
  }

  override def findById(id: Int): Future[Option[Person]] = {
    val queryDescription = persons.filter(_.id === id).result.headOption

    logger.info("Trying to find scout with id: {}", id)
    dbConnector.db.run(queryDescription).andThen {
      case Failure(e) => logger.error("Error finding the scout with id: {}, error: {}", id, e.getMessage)
      case Success(_) => logger.info("Scout with id: {} was found with success", id)
    }
  }
}
