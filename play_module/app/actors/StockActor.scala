package actors
import akka.actor.{Actor, ActorRef, Props}
import utils.{FakeStockQuote, StockQuote}
import java.util.{Date, Random}

import model.{Quotation, Quotations}
import org.joda.time.{DateTime, Days}

import scala.collection.immutable.{HashSet, Queue}
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import play.libs.Akka

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}

/**
  * There is one StockActor per stock symbol.  The StockActor maintains a list of users watching the stock and the stock
  * values.  Each StockActor updates a rolling dataset of randomly generated stock values.
  */

class StockActor(symbol: String) extends Actor {

  lazy val stockQuote: StockQuote = new FakeStockQuote

  protected[this] var watchers: HashSet[ActorRef] = HashSet.empty[ActorRef]

  // A random data set which uses stockQuote.newPrice to get each data point
  var stockHistory: Queue[java.lang.Double] = {
    lazy val initialPrices: Stream[java.lang.Double] = (new Random().nextDouble * 800) #:: initialPrices.map(previous => stockQuote.newPrice(previous))
    initialPrices.take(50).to[Queue]
  }

  // Fetch the latest stock value every 75ms
  val stockTick = context.system.scheduler.schedule(Duration.Zero, 75.millis, self, FetchLatest)

  def receive = {
    case FetchLatest =>
      // add a new stock price to the history and drop the oldest
      val newPrice = stockQuote.newPrice(stockHistory.last.doubleValue())
      stockHistory = stockHistory.drop(1) :+ newPrice
      // notify watchers
      watchers.foreach(_ ! StockUpdate(symbol, newPrice))
    case WatchStock(_) =>
      // send the stock history to the user
      sender ! StockHistory(symbol, stockHistory.asJava)
      // add the watcher to the list
      watchers = watchers + sender
    case UnwatchStock(_) =>
      watchers = watchers - sender
      if (watchers.size == 0) {
        stockTick.cancel()
        context.stop(self)
      }
  }
}

class StocksActor extends Actor {
  def receive = {
    case watchStock @ WatchStock(symbol) =>
      // get or create the StockActor for the symbol and forward this message
      context.child(symbol).getOrElse {
        context.actorOf(Props(new StockActor(symbol)), symbol)
      } forward watchStock
    case unwatchStock @ UnwatchStock(Some(symbol)) =>
      // if there is a StockActor for the symbol forward this message
      context.child(symbol).foreach(_.forward(unwatchStock))
    case unwatchStock @ UnwatchStock(None) =>
      // if no symbol is specified, forward to everyone
      context.children.foreach(_.forward(unwatchStock))
  }




  def calculateMovingAveragesOnActorSystem(companiesData: Future[List[Quotation]], dateFrom: java.util.Date, dateTo: java.util.Date) = {
    val start = new DateTime(dateFrom)
    val end = new DateTime(dateTo)
    var stockValuesForPeriodDateFromDateTo = new ListBuffer[Double]

    io.lamma.Date(dateFrom.getYear(),dateFrom.getMonth(),dateFrom.getDay()) to io.lamma.Date(dateTo.getYear(),dateTo.getMonth(),dateTo.getDay()) map(date =>
      stockValuesForPeriodDateFromDateTo+=this
      .calculateAverageValueForStockForGivenDay(companiesData, new java.util.Date(date.yyyy,date.mm,date.dd)))

    this.calculateMovingAveragesForIndex(stockValuesForPeriodDateFromDateTo.toList)
  }

  def calculateAverageFromMovingAveragesForIndex(closingDayPrices: List[Double]): Double = {
    this.calculateWeightedMovingAverage(closingDayPrices)
  }

  def calculateWeightedMovingAverage(closingDayPrices: List[Double])={
    val sum: Double= closingDayPrices.sum
    sum/closingDayPrices.size
  }

  def calculateMovingAveragesForIndex(closingDayIndexPricesForLongerPeriod: List[Double])={
    val closingDayPricesForLongerPeriodAmount = closingDayIndexPricesForLongerPeriod.size
    var middleSlot = closingDayPricesForLongerPeriodAmount/2
    var results = new ListBuffer[Double]()

    for( i <- 0 until middleSlot) {
      val listToBePassed = closingDayIndexPricesForLongerPeriod.slice(i, middleSlot)
      var resultToBeAppended = calculateWeightedMovingAverage(listToBePassed)
      results += resultToBeAppended
      if(middleSlot<=closingDayPricesForLongerPeriodAmount){
        middleSlot+=1
      }
    }
    results
  }

  /**
    *
    * @param companiesData
    * @param date
    * @return
    */

  def calculateAverageValueForStockForGivenDay(companiesData: Future[List[Quotation]], date: java.util.Date) = {
    var counter = 0;
    var stockValueForGivenDay: Float = 0;
    val fivemin = 5.minute
    val listFromFuture =  Await.result(companiesData, fivemin)
//    val listFromFuture = companiesData.result(fivemin)
    //Dirty hack!!!!
    listFromFuture.map(q=>if(q.date.equals(date)){
      stockValueForGivenDay+=q.closing
      counter+=1
    })

    stockValueForGivenDay/counter
  }


}

object StocksActor {
  lazy val stocksActor: ActorRef = Akka.system.actorOf(Props(classOf[StocksActor]))
}

/**
  * u
  *
  * @param symbol
  * @param companiesData
  */
case class MovingAverageCalculation(symbol: String, companiesData: List[Quotation])

case object FetchLatest

case class StockUpdate(symbol: String, price: Number)

case class StockHistory(symbol: String, history: java.util.List[java.lang.Double])

case class WatchStock(symbol: String)

case class UnwatchStock(symbol: Option[String])