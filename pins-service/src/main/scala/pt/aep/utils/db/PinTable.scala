package pt.aep.utils.db

import pt.aep.domain.entities.Pin


trait PinTable {

  protected val databaseConnector: DBConnector
  import slick.jdbc.PostgresProfile.api._

  class Pins(tag: Tag) extends Table[Pin](tag, "pins") {
    def id        = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def pinType   = column[String]("type")
    def jId       = column[String]("jid")
    def lat       = column[Double]("lat")
    def long      = column[Double]("long")
    def personId  = column[Int]("person_id")

  def * = (pinType, jId, lat, long, personId) <> ((Pin.apply _).tupled, Pin.unapply)
}

  lazy val pins = TableQuery[Pins]

}
