package pt.aep.config.exceptions

case class ReadApplicationConfigException(message: String) extends RuntimeException(message)
