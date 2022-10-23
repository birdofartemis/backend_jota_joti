package pt.aep.utils.db

import pt.aep.domain.entities.Person

trait PersonTable {

  protected val databaseConnector: DBConnector
  import databaseConnector.profile.api._

  class Persons(tag: Tag) extends Table[Person](tag, "persons") {
    def id        = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName  = column[String]("last_name")
    def group     = column[Int]("group")
    def division  = column[String]("division")

    def * = (firstName, lastName, group, division) <> ((Person.apply _).tupled, Person.unapply)
  }

  protected val persons = TableQuery[Persons]

}
