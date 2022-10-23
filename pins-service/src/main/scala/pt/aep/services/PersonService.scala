package pt.aep.services

import pt.aep.domain.entities.Person

import scala.concurrent.Future

trait PersonService {
  def createPerson(person: Person): Future[Int]
  def findById(id: Int): Future[Option[Person]]
}
