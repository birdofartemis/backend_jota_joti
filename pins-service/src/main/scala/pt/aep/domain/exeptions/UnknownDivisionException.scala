package pt.aep.domain.exeptions

case class UnknownDivisionException (message: String) extends RuntimeException(message)
