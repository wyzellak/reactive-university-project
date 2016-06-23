package actors

import java.text.SimpleDateFormat
import java.time.{LocalDate, ZoneId}
import java.util.Date

import akka.actor._
import akka.routing.RoundRobinPool
import akka.util.Timeout
import model.{IndexName, Quotation}
import model.IndexName._
import services.QuotationService

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

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
  var listOfResults: ListBuffer[Double] = ListBuffer()
  var resultsCount: Integer = 0
  val companiesAmount: Integer = companyNames.length

  val workerRouter = context.actorOf(
    Props[Worker].withRouter(RoundRobinPool(workersAmount)), name = "workerRouterPool")

  context.setReceiveTimeout(5 seconds)

  def notifyListener() = {

    // Send the result to the listener
    listener ! StockIndexValue(listOfResults, indexName, companyNames)
    // Stops this actor and all its supervised children
    context.stop(self)

  }

  def receive = {

    case Calculate =>
      for (i <- 0 until companiesAmount ) workerRouter ! Work(indexName, companyNames(i))

    case PartialResult(value) =>
      listOfResults += value
      resultsCount += 1
      if (resultsCount == companiesAmount) this.notifyListener()

    case ReceiveTimeout =>
      resultsCount += 1
      if (resultsCount == companiesAmount) this.notifyListener()

  }

}

class Worker extends Actor {

  def calculateStockIndexFor(indexName: IndexName, companyName: String): Double = {

    val result : Double = indexName match {

      case IndexName.AVERAGE_TRUE_RANGE => {

//        var sleepOneToTenSeconds = 1000 + scala.util.Random.nextInt( (10000 - 1000) + 1 )
//        Thread.sleep(sleepOneToTenSeconds)

        var res : Double = 0.0

        for( a <- 1 to 1000) {

          val format = new SimpleDateFormat("dd/MM/yyy")
          var date = new java.util.Date()

          date = format.parse("01/04/2016")

          val quotation: Future[Seq[Quotation]] = QuotationService.getByCompanyName(companyName)
          res = StockIndexAlgorithms.calculateAverageValueForStockForGivenDay(quotation, date)

        }

        res

      }

      case IndexName.EASE_OF_MOVEMENT => {

//        var sleepOneToTenSeconds = 1000 + scala.util.Random.nextInt( (10000 - 1000) + 1 )
//        Thread.sleep(sleepOneToTenSeconds)

        var res: Double = 0.0

        for( a <- 1 to 1000) {

          val format = new SimpleDateFormat("dd/MM/yyy")
          var pastFromDate = new java.util.Date()
          var pastToDate = new java.util.Date()
          var presentFromDate = new java.util.Date()
          var presentToDate = new java.util.Date()

          pastFromDate = format.parse("01/01/2016")
          pastToDate = format.parse("06/04/2017")
          presentFromDate = format.parse("01/01/2016")
          presentToDate = format.parse("06/04/2016")

          val quotation: Future[Seq[Quotation]] = QuotationService.getByCompanyName(companyName)
          res = StockIndexAlgorithms.calculateEaseOfMovement(quotation, pastFromDate, pastToDate, presentFromDate, presentToDate)

        }

        res

      }

      case IndexName.MOVING_AVERAGE => {

//        var sleepOneToTenSeconds = 1000 + scala.util.Random.nextInt( (10000 - 1000) + 1 )
//        Thread.sleep(sleepOneToTenSeconds)

        var res: Double = 0.0

        for( a <- 1 to 1000) {

          val format = new SimpleDateFormat("dd/MM/yyy")
          var fromDate = new java.util.Date()
          var toDate = new java.util.Date()

          fromDate = format.parse("01/01/2016")
          toDate = format.parse("06/04/2017")

          var resBuffer = new ListBuffer[Double]()
          val quotation: Future[Seq[Quotation]] = QuotationService.getByCompanyName(companyName)
          resBuffer = StockIndexAlgorithms.calculateMovingAveragesOnActorSystem(quotation, fromDate, toDate)
          res = resBuffer.head

        }

        res

      }

      case _ => 0.0

    }

    result

  }

  def receive = {
    case Work(indexName: IndexName, companyName: String) => {
      val future = sender ! PartialResult(calculateStockIndexFor(indexName, companyName)) // Perform the work
    }
  }
}

class Listener extends Actor {
  def receive = {
    case StockIndexValue(listOfResults, indexName, companyNames) =>

      println("\n\n----------------------------------------------")
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
