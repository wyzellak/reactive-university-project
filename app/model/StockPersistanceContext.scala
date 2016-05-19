package model

import net.fwbrasil.activate.play.ActivatePlayContext
import net.fwbrasil.activate.storage.relational.PooledJdbcRelationalStorage
import net.fwbrasil.activate.storage.relational.idiom.mySqlDialect

/**
  * @author alisowsk
  */
object StockPersistenceContext extends ActivatePlayContext{
  override val storage = new PooledJdbcRelationalStorage {
    val jdbcDriver = "org.mariadb.jdbc.Driver"
    val user = Some("root")
    val password = Some("root")
    val url = "jdbc:mariadb://127.0.0.1:3306/stock_exchange"
    val dialect = mySqlDialect
  }
}