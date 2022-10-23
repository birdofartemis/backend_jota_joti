package pt.aep.domain.exeptions

case class UnknownGroupException (message: String) extends RuntimeException(message)
