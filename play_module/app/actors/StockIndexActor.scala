package actors

import akka.actor._
import akka.routing.RoundRobinPool

sealed trait StockIndexMessage

case object Calculate extends StockIndexMessage
case class Work(number: Double) extends StockIndexMessage
case class PartialResult(value: Double) extends StockIndexMessage
case class StockIndexValue(value: Double, tickerSymbol: String)

/**
  * The main object for stock index calculations
  *
  * @author Piotr Kluch
  */
object StockIndexActor extends App {

  /**
    * Calculates the stock market index value
    *
    * @param workersAmount The amount of worker actors for computation
    * @param numbers Numbers (data) for the computation
    * @param tickerSymbol Ticker symbol for the particular stock on the market
    */
  def calculate(workersAmount: Int, numbers: List[Double], tickerSymbol: String) {

    // Create an Akka system
    val system = ActorSystem("StockIndexActorSystem")

    // Create the result listener, which will print the result and shutdown the system
    val listener = system.actorOf(Props[Listener], name = "listener")

    // Create the master actor (a seed node, sort of)
    val master = system.actorOf(Props(new Master(workersAmount, numbers, tickerSymbol, listener)), name = "master")

    // Start the calculation
    master ! Calculate

  }
}

class Master(workersAmount: Int, numbers: List[Double], stockMarketIndex: String, listener: ActorRef) extends Actor {
  var calculationResult: Double = 0.0
  var resultsCount: Integer = 0
  val numbersAmount: Integer = numbers.length

  val workerRouter = context.actorOf(
    Props[Worker].withRouter(RoundRobinPool(workersAmount)), name = "workerRouterPool")

  def receive = {
    case Calculate =>
      for (i <- 0 until numbersAmount ) workerRouter ! Work(numbers(i))
    case PartialResult(value) =>
      calculationResult += value
      resultsCount += 1
      if (resultsCount == numbersAmount) {
        // Send the result to the listener
        listener ! StockIndexValue(calculationResult, stockMarketIndex)
        // Stops this actor and all its supervised children
        context.stop(self)
      }
  }

}

class Worker extends Actor {
  def calculateStockIndexFor(number: Double): Double = {
    var result = number * Math.E
    result
  }

  def receive = {
    case Work(number: Double) =>
      sender ! PartialResult(calculateStockIndexFor(number)) // Perform the work
  }
}

class Listener extends Actor {
  def receive = {
    case StockIndexValue(stockIndexValue, stockMarketIndex) =>
      println("\n\tStock index approximation: \t\t%s\n\tFor stock symbol: %s".format(stockIndexValue, stockMarketIndex))
      context.system.shutdown()
  }
}
