package pt.aep.domain.entities

final case class Pin(pinType: String, jId: String, lat: Double, long: Double, personId: Int){
  val POSSIBLE_PIN_TYPES: List[String] = List("RADIO", "WEB")

  require(POSSIBLE_PIN_TYPES.contains(pinType), s"PinType: $pinType is unknown! The known pinTypes are: " +
    s"${POSSIBLE_PIN_TYPES.toString}")
  require(personId > 0, s"Person id: $personId is not bigger than zero!")
  require(jId.nonEmpty, "JID can not be empty!")
}