package pt.aep

import akka.actor.ActorSystem
import akka.event.{LogSource, Logging}
import akka.http.scaladsl.Http
import org.slf4j.{Logger, LoggerFactory}
import pt.aep.config.Config
import pt.aep.http.HttpRoute
import pt.aep.services.{PersonServiceImpl, PinServiceImpl}
import pt.aep.utils.db.DBConnector

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Main extends App {

  def startApplication() = {
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val executor: ExecutionContext = actorSystem.dispatcher
    implicit val logSource: LogSource[AnyRef] = new LogSource[AnyRef] {
      def genString(o: AnyRef): String = o.getClass.getName
      override def getClazz(o: AnyRef): Class[_] = o.getClass
    }
    import akka.event.Logging
    val log = Logging(actorSystem, this)

    val config = Config.load()

    log.info("Trying to connect to Database...")
    val databaseConnector = new DBConnector(
      config.database.url,
      config.database.username,
      config.database.password
    )

    log.info("Trying to migrate tables to database")
    databaseConnector.migrateDatabaseSchema()

    val personsService = new PersonServiceImpl(databaseConnector)
    val pinsService = new PinServiceImpl(databaseConnector)
    val httpRoute = new HttpRoute(personsService, pinsService)
    Http().newServerAt(config.http.host, config.http.port).bind(httpRoute.route).andThen {
      case Failure(e) => log.error("Failed to start app, error: {}", e.getMessage)
      case Success(server) => log.info("Server initialized on port: {}", server.localAddress.getPort)
    }
  }
  startApplication()
}
