package actors

import model.Quotation
import org.joda.time.DateTime

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by peter on 22/06/16.
  */
object StockIndexAlgorithms {


  /** 1 - BEGIN CALCULATE MOVING AVERAGE (from date to date) **/

  /**
    *
    * @param companiesData
    * @param dateFrom
    * @param dateTo
    * @return
    */

  def calculateMovingAveragesOnActorSystem(companiesData: Future[Seq[Quotation]], dateFrom: java.util.Date, dateTo: java.util.Date) = {
    val start = new DateTime(dateFrom)
    val end = new DateTime(dateTo)
    var stockValuesForPeriodDateFromDateTo = new ListBuffer[Double]

    io.lamma.Date(dateFrom.getYear(),dateFrom.getMonth(),dateFrom.getDay()) to io.lamma.Date(dateTo.getYear(),dateTo.getMonth(),dateTo.getDay()) map(date =>
      stockValuesForPeriodDateFromDateTo+=this
        .calculateAverageValueForStockForGivenDay(companiesData, new java.util.Date(date.yyyy,date.mm,date.dd)))

    this.calculateMovingAveragesForIndex(stockValuesForPeriodDateFromDateTo.toList)
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

  /** 1 - END CALCULATE MOVING AVERAGE **/


  /** 2 - BEGIN CALCULATE AVERAGE VALUE (for single date (day granularity)) **/

  /**
    *
    * @param companiesData
    * @param date
    * @return
    */

  def calculateAverageValueForStockForGivenDay(companiesData: Future[Seq[Quotation]], date: java.util.Date) : Double = {
    var counter = 0;
    var stockValueForGivenDay: Double = 0;
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

  /** 2 - END CALCULATE AVERAGE VALUE **/


  /** 3 - BEGIN CALCULATE EASE OF MOVEMENT (from date to date, from date to date) **/

  def calculateMaxValueOfCompanyForGivenPeriod(companiesData: Future[Seq[Quotation]], dateFrom: java.util.Date, dateTo: java.util.Date): Double = {
    var counter = 0;
    var stockValueForGivenDay: Double = 0;
    val fivemin = 5.minute
    val listFromFuture: Seq[Quotation] =  Await.result(companiesData, fivemin)
    var selectedQuotations = new ListBuffer[Quotation]
    var maxValues = new ListBuffer[Double]
    //    val listFromFuture = companiesData.result(fivemin)
    //Dirty hack!!!!
    io.lamma.Date(dateFrom.getYear(),dateFrom.getMonth(),dateFrom.getDay()) to io.lamma.Date(dateTo.getYear(),dateTo.getMonth(),dateTo.getDay()) map(date=>listFromFuture.map(q=>if (q.date.equals(date)){
      selectedQuotations += q
    }))
    selectedQuotations.map(q=>maxValues+=q.max)
    return maxValues.max
  }

  def calculateMinValueOfCompanyForGivenPeriod(companiesData: Future[Seq[Quotation]], dateFrom: java.util.Date, dateTo: java.util.Date): Double = {
    var counter = 0;
    var stockValueForGivenDay: Double = 0;
    val fivemin = 5.minute
    val listFromFuture: Seq[Quotation] =  Await.result(companiesData, fivemin)
    var selectedQuotations = new ListBuffer[Quotation]
    var minValues = new ListBuffer[Double]
    //    val listFromFuture = companiesData.result(fivemin)
    //Dirty hack!!!!
    io.lamma.Date(dateFrom.getYear(),dateFrom.getMonth(),dateFrom.getDay()) to io.lamma.Date(dateTo.getYear(),dateTo.getMonth(),dateTo.getDay()) map(date=>listFromFuture.map(q=>if (q.date.equals(date)){
      selectedQuotations += q
    }))
    selectedQuotations.map(q=>minValues+=q.min)
    return minValues.min
  }

  /**
    *
    * @param companiesData
    * @param pastDateFrom
    * @param pastDateTo
    * @param presentDateFrom
    * @param presentDateTo
    * @return
    */

  def calculateEaseOfMovement(companiesData: Future[Seq[Quotation]], pastDateFrom: java.util.Date, pastDateTo: java.util.Date, presentDateFrom: java.util.Date, presentDateTo: java.util.Date): Double ={

    val maxPresent = this.calculateMaxValueOfCompanyForGivenPeriod(companiesData, presentDateFrom, presentDateTo)
    val minPresent = this.calculateMinValueOfCompanyForGivenPeriod(companiesData, presentDateFrom, presentDateTo)

    val maxPast = this.calculateMaxValueOfCompanyForGivenPeriod(companiesData, pastDateFrom, pastDateTo)
    val minPast = this.calculateMinValueOfCompanyForGivenPeriod(companiesData, pastDateFrom, pastDateTo)

    return ((maxPresent+minPresent)/2 - (maxPast+minPast)/2)/maxPresent-minPresent

  }

  /** 3 - END CALCULATE AVERAGE VALUE **/

}
