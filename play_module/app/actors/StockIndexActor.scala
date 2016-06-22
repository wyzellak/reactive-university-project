package actors

import java.time.{LocalDate, ZoneId}
import java.util.Date

import akka.actor._
import akka.routing.RoundRobinPool
import model.{IndexName, Quotation}
import model.IndexName._
import services.QuotationService

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

sealed trait StockIndexMessage

case object Calculate extends StockIndexMessage
case class Work(indexName: IndexName, companyName: String) extends StockIndexMessage
case class PartialResult(value: Double) extends StockIndexMessage
case class StockIndexValue(listOfResults: ListBuffer[Double], indexName: IndexName, companyName: Seq[String])

/**
  * The main object for stock index calculations
  *
  * @author Piotr Kluch
  */
object StockIndexActor {

  /**
    * Calculates the stock market index value
    *
    * @param workersAmount The amount of worker actors for computation
    * @param indexName Index name used for computation
    * @param companyNames Ticker symbols for the particular stock on the market
    */
  def calculateUsingActorsWithWorkersAmountOf(workersAmount: Int, indexName: IndexName, companyNames: Seq[String]) = {

    // Create an Akka system
    val system = ActorSystem("StockIndexActorSystem")

    // Create the result listener, which will print the result and shutdown the system
    val listener = system.actorOf(Props[Listener], name = "listener")

    // Create the master actor (a seed node, sort of)
    val master = system.actorOf(Props(new Master(workersAmount, indexName, companyNames, listener)), name = "master")

    // Start the calculation
    master ! Calculate

    // Meet the IndexService Future value requirement
    Future.successful(1.0)

  }

}

class Master(workersAmount: Int, indexName: IndexName, companyNames: Seq[String], listener: ActorRef) extends Actor {
  var calculationResult: Double = 0.0
  var listOfResults: ListBuffer[Double] = ListBuffer()
  var resultsCount: Integer = 0
  val companiesAmount: Integer = companyNames.length

  val workerRouter = context.actorOf(
    Props[Worker].withRouter(RoundRobinPool(workersAmount)), name = "workerRouterPool")

  def receive = {

    case Calculate =>
      for (i <- 0 until companiesAmount ) workerRouter ! Work(indexName, companyNames(i))

    case PartialResult(value) =>
      calculationResult += value
      listOfResults += value
      resultsCount += 1
      if (resultsCount == companiesAmount) {
        // Send the result to the listener
        listener ! StockIndexValue(listOfResults, indexName, companyNames)
        // Stops this actor and all its supervised children
        context.stop(self)
      }

  }

}

class Worker extends Actor {

  def calculateStockIndexFor(indexName: IndexName, companyName: String): Double = {

    val result : Double = indexName match {

      case IndexName.AVERAGE_TRUE_RANGE => {

        var res : Double = 12345.0
//        val date = Date.from(LocalDate.of(2016,1,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
//        val quotation: Future[Seq[Quotation]] = QuotationService.getByCompanyName(companyName)
//        res = StockIndexAlgorithms.calculateAverageValueForStockForGivenDay(quotation, date)
        res

      }

      case IndexName.EASE_OF_MOVEMENT => {

        var res : Double = 12345.0
//        val pastFromDate = Date.from(LocalDate.of(2016,1,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
//        val pastToDate = Date.from(LocalDate.of(2016,2,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
//        val presentFromDate = Date.from(LocalDate.of(2016,3,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
//        val presentToDate = Date.from(LocalDate.of(2016,4,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
//        val quotation: Future[Seq[Quotation]] = QuotationService.getByCompanyName(companyName)
//        res = StockIndexAlgorithms.calculateEaseOfMovement(quotation, pastFromDate, pastToDate, presentFromDate, presentToDate)
        res

      }

      case IndexName.MOVING_AVERAGE => {

        var res : Double = 12345.0
//        var resBuffer = new ListBuffer[Double]()
//        val fromDate = Date.from(LocalDate.of(2016,1,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
//        val toDate = Date.from(LocalDate.of(2017,1,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
//        val quotation: Future[Seq[Quotation]] = QuotationService.getByCompanyName(companyName)
//        resBuffer = StockIndexAlgorithms.calculateMovingAveragesOnActorSystem(quotation, fromDate, toDate)
//        res = resBuffer.head
        res

      }

      case _ => 0.0

    }

    result

  }

  def receive = {
    case Work(indexName: IndexName, companyName: String) =>
      sender ! PartialResult(calculateStockIndexFor(indexName, companyName)) // Perform the work
  }
}

class Listener extends Actor {
  def receive = {
    case StockIndexValue(listOfResults, indexName, companyNames) =>

      println("\n\t-===============================-"
            + "\n\tStock index approximation results"
            + "\n\tFor stock index %s".format(indexName)
            + "\n\t-===============================-")

      for ( (name, value) <- companyNames zip listOfResults) {
        println("\n\tFor ticker symbol (company name): %s".format(name))
        println("\tValue of index %s: %s".format(indexName, value.toString()))
      }

      context.system.shutdown()
  }
}
