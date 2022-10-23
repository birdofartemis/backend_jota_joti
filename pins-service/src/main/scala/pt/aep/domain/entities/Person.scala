package pt.aep.domain.entities

import pt.aep.domain.entities.Person._


final case class Person(firstName: String, lastName: String, group: Int, division: String){
  require(firstName.nonEmpty, "Name can not be empty!")
  require(lastName.nonEmpty, "Last name can not be empty!")
  require(isGroupValid(group), s"Group: $group is unknown! The known groups are: ${POSSIBLE_GROUPS.toString}")
  require(isDivisionValid(division), s"Division: $division is unknown! The known divisions are: ${POSSIBLE_DIVISIONS.toString}")
}

object Person {
  private val POSSIBLE_GROUPS: List[Int] = List(39, 63, 80, 97, 111, 126, 137, 186, 193, 209, 227, 238)
  private val POSSIBLE_DIVISIONS: List[String] = List("ALCATEIA", "TRIBO_ESCOTEIROS", "TRIBO_EXPLORADORES", "CLA")

  def isGroupValid(group: Int): Boolean = POSSIBLE_GROUPS.contains(group)
  def isDivisionValid(division: String): Boolean = POSSIBLE_DIVISIONS.contains(division)
}


