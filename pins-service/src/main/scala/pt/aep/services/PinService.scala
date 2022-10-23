package pt.aep.services

import pt.aep.domain.entities.Pin

import scala.concurrent.Future

trait PinService {
  def createPin(pin: Pin): Future[Int]
  def deletePin(pinId: Int): Future[Int]
  def getPinsByGroup(group: Int): Future[Seq[Pin]]
  def getPinsByDivision(division: String): Future[Seq[Pin]]
  def getResultsByGroup: Future[Map[String, Int]]
  def getResultsByDivision: Future[Map[String, Int]]
}
