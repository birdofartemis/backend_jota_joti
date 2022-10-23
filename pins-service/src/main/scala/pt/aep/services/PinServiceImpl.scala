package pt.aep.services
import org.slf4j.{Logger, LoggerFactory}
import pt.aep.domain.entities.{Person, Pin}
import pt.aep.domain.exeptions.{UnknownDivisionException, UnknownGroupException}
import pt.aep.utils.db.exceptions.QueryTransformationException
import pt.aep.utils.db.{DBConnector, PersonTable, PinTable}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class PinServiceImpl(dbConnector: DBConnector)(implicit ec: ExecutionContext)
  extends PinService with PinTable with PersonTable {
  protected val databaseConnector: DBConnector = dbConnector
  val logger: Logger = LoggerFactory.getLogger(classOf[PinServiceImpl])

  import slick.jdbc.PostgresProfile.api._

  def createPin(pin: Pin): Future[Int] = {
    logger.info("Trying to insert pin with JID: {}", pin.jId)
    val queryDescription = (pins returning pins.map(_.id)) += pin
    dbConnector.db.run(queryDescription).andThen {
      case Failure(e) => logger.error("Error inserting pin with JID: {}... Error: {}", pin.jId, e.getMessage)
      case Success(id) => logger.info("Pin: {} was inserted with success with id: {}", pin.jId, id)
    }
  }

  def deletePin(pinId: Int): Future[Int] = {
    logger.info("Trying to delete pin with id: {}", pinId)
    val queryDescription = pins.filter(_.id === pinId).delete

    dbConnector.db.run(queryDescription).andThen {
      case Failure(e) => logger.error("Error deleting pin with id: $pinId, error: {}", e.getMessage)
      case Success(_) => logger.info("Pin with id: {} was deleted with success", pinId)
    }
  }

  def getPinsByGroup(group: Int): Future[Seq[Pin]] = {
    logger.info("Validating group: {}...", group)
    if(!Person.isGroupValid(group)) {
      logger.error("The group: {} does not exist!", group)
      throw UnknownGroupException(s"The group: $group does not exist!")
    }
    logger.info("Trying to get pins from group {}", group)
    val queryDescription = {
      val innerJoin = for {
        (pin, scout) <- pins join persons on (_.personId === _.id)
      } yield (scout.group, pin)
      innerJoin.filter(_._1 === group).map(_._2).result
    }
    dbConnector.db.run(queryDescription).andThen {
      case Failure(e) => logger.error("Error while getting pins from group {}, error: {}", group, e.getMessage)
      case Success(_) => logger.info("Pins from group {} were got with success", group)
    }
  }

  def getPinsByDivision(division: String): Future[Seq[Pin]] = {
    logger.info("Validating division: {}...", division)
    if(!Person.isDivisionValid(division)) {
      logger.error("The division: {} does not exist!", division)
      throw UnknownDivisionException(s"The division: $division does not exist!")
    }

    logger.info("Trying to get pins from division {}", division)
    val queryDescription = {
      val innerJoin = for {
        (pin, scout) <- pins join persons on (_.personId === _.id)
      } yield (scout.division, pin)
      innerJoin.filter(_._1 === division).map(_._2).result
    }
    dbConnector.db.run(queryDescription).andThen {
      case Failure(e) => logger.error("Error while getting pins from division {}, error: {}", division, e.getMessage)
      case Success(_) => logger.info("Pins from division {} were got with success", division)
    }
  }

  def getResultsByGroup: Future[Map[String, Int]] = {
    logger.info(s"Trying to get results of groups")
    val queryDescription = for {
        (pin, scout) <- pins join persons on (_.personId === _.id)
      } yield (scout.group, pin)

    dbConnector.db.run(queryDescription.result).transform {
      case Failure(e) => throw QueryTransformationException(e.getMessage)
      case Success(results) => Try(Map(results.groupBy(_._1).map(elem => (s"${elem._1}", elem._2.size)).toSeq.sortWith(_._2 > _._2):_*))
    }.andThen {
      case Failure(e) => logger.error("Error while grouping the results per group, error: {}", e.getMessage)
      case Success(_) => logger.info("The results per group were got with success")
    }
  }

  override def getResultsByDivision: Future[Map[String, Int]] = {
    logger.info(s"Trying to get results of divisions")
    val queryDescription = for {
      (pin, scout) <- pins join persons on (_.personId === _.id)
    } yield (scout.division, pin)

    dbConnector.db.run(queryDescription.result).transform {
      case Failure(e) => throw QueryTransformationException(e.getMessage)
      case Success(results) => Try(Map(results.groupBy(_._1).map(elem => (s"${elem._1}", elem._2.size)).toSeq.sortWith(_._2 > _._2):_*))
    }.andThen {
      case Failure(e) => logger.error("Error while grouping the results per division, error: {}", e.getMessage)
      case Success(_) => logger.info("The results per division were got with success")
    }
  }
}
