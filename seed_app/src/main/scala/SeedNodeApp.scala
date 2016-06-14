import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props

/**
  * Created by antosikj (Jakub Antosik) on 02/06/16.
  */
object SeedNodeApp {
  def main(args: Array[String]) {
    SeedNodeListener.main(Array("2551"))
    SeedNodeListener.main(Array("2552"))
    SeedNodeListener.main(Array("2553"))
    SeedNodeListener.main(Array("0"))
    SeedNodeListener.main(Array("0"))
  }
}
