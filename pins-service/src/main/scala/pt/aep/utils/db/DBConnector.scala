package pt.aep.utils.db

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.flywaydb.core.Flyway


class DBConnector(dbUrl: String, dbUser: String, dbPassword: String) {
  private val hikariDataSource: HikariDataSource = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(dbUrl)
    hikariConfig.setUsername(dbUser)
    hikariConfig.setPassword(dbPassword)

    new HikariDataSource(hikariConfig)
  }

  val profile = slick.jdbc.PostgresProfile

  import profile.api._
  val db = Database.forDataSource(hikariDataSource, None)
  db.createSession()


  //migration
  private val flyway = new Flyway()
  flyway.setDataSource(dbUrl, dbUser, dbPassword)

  def migrateDatabaseSchema(): Unit = flyway.migrate()

  def dropDatabase(): Unit = flyway.clean()
}
