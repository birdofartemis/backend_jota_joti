package pt.aep.config

import org.slf4j.{Logger, LoggerFactory}
import pt.aep.config.entities.{DBConfig, HttpConfig}
import pt.aep.config.exceptions.ReadApplicationConfigException
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class Config(http: HttpConfig, database: DBConfig)

object Config {
  val logger: Logger = LoggerFactory.getLogger(classOf[Config])
  def load(): Config = {
    logger.info("Loading configurations...")
    ConfigSource.default.load[Config] match {
      case Left(error) => throw ReadApplicationConfigException("Cannot read config file, errors:\n" + error.toList.mkString("\n"))
      case Right(config) => config
    }
  }
}
