package services

import java.time.{LocalDate, ZoneId}
import java.util.Date

import actors.{StockIndexActor, StocksActor}
import akka.actor.{ActorSystem, Props}
import model.IndexName.IndexName
import model.{IndexName, Quotation}
import play.api.libs.concurrent.Execution.Implicits._
import services.QuotationService.listAllQuotations

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * @author alisowsk
  */
object IndexService {

  def runIndex(indexName: IndexName, companyNames: Seq[String]): Future[Double] = indexName match {
    case IndexName.TEST => {
      val fromDate = Date.from(LocalDate.of(2016,1,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
      val toDate = Date.from(LocalDate.of(2016,2,1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)

      calculateTest(companyNames, fromDate, toDate)
    }

    case IndexName.AVERAGE_TRUE_RANGE => calculateAverageTrueRange(indexName, companyNames)

    case IndexName.EASE_OF_MOVEMENT => calculateEaseOfMovement(indexName, companyNames)

    case IndexName.MOVING_AVERAGE => calculateMovingAverage(indexName, companyNames)

    case default => Future.successful(0.0)
  }

  private def calculateTest(companyNames: Seq[String], dateFrom: Date, dateTo: Date): Future[Double] = {
    val quotation: Future[Seq[Quotation]] = QuotationService.getByCompanyName(companyNames.head)
    val quotations = QuotationService.getByCompanyNames(companyNames)

    val valuesSingleQuotation = Await.ready(quotation, Duration.Inf).value.get.get.map($ => $.closing toDouble).toList
    val valuesMultipleQuotations = Await.ready(quotations, Duration.Inf).value.get.get.map($ => $.closing toDouble).toList
    val valuesMultipleQuotationsByDate = Await.ready(quotations, Duration.Inf).value.get.get
      .filter(q => (q.date.after(dateFrom) && q.date.before(dateTo)) || q.date.eq(dateFrom) || q.date.equals(dateTo)).map($ => $.closing toDouble).toList

    val testValues = getTestData()

    Future.successful(1.0)
  }

  private def calculateAverageTrueRange(indexName: IndexName, companyNames: Seq[String]): Future[Double] = {

    StockIndexActor.calculateUsingActorsWithWorkersAmountOf(workersAmount = 4, indexName, companyNames)

  }

  private def calculateEaseOfMovement(indexName: IndexName, companyNames: Seq[String]): Future[Double] = {

    StockIndexActor.calculateUsingActorsWithWorkersAmountOf(workersAmount = 4, indexName, companyNames)

  }

  private def calculateMovingAverage(indexName: IndexName, companyNames: Seq[String]): Future[Double] = {

    StockIndexActor.calculateUsingActorsWithWorkersAmountOf(workersAmount = 4, indexName, companyNames)

    //TODO calculateMovingAveragesOnActorSystem(QuotationService.listAllQuotations , dateFrom, dateTo)
    //TODO invoke calculateMovingAveragesOnActorSystem(companiesData: List[Quotation], dateFrom: java.util.Date, dateTo: java.util.Date)
    //from actor system  - will return list of values for date interval for given stock
    //TODO calculateAverageFromMovingAveragesForIndex() - to powinno zwrocic wynik

    Future.successful(1.0)

  }

  private def getTestData(): List[Double] = {
    List(24.280000686645508, 24.459999084472656, 24.5, 24.68000030517578,
      24.280000686645508, 24.489999771118164, 24.450000762939453, 24.270000457763672,
      24.510000228881836, 24.350000381469727, 24.06999969482422, 24.579999923706055,
      24.43000030517578, 23.6200008392334, 23.260000228881836, 23.469999313354492,
      23.219999313354492, 23.579999923706055, 23.049999237060547, 23.65999984741211,
      24.219999313354492, 24.270000457763672, 24.700000762939453, 24.649999618530273,
      23.989999771118164, 23.299999237060547, 23.399999618530273, 23.0, 22.860000610351562,
      23.25, 22.950000762939453, 22.760000228881836, 22.770000457763672, 23.350000381469727,
      23.459999084472656, 23.469999313354492, 23.440000534057617, 23.709999084472656,
      23.420000076293945, 23.75, 23.440000534057617, 22.5, 23.0, 23.270000457763672, 23.280000686645508,
      22.700000762939453, 22.899999618530273, 23.5, 22.690000534057617, 22.3799991607666, 22.559999465942383,
      21.8700008392334, 21.969999313354492, 22.0, 21.780000686645508, 22.06999969482422, 21.670000076293945,
      21.239999771118164, 21.790000915527344, 21.6299991607666, 21.299999237060547, 21.540000915527344,
      21.989999771118164, 21.510000228881836, 20.5, 20.84000015258789, 21.0, 21.020000457763672, 21.0,
      20.459999084472656, 20.690000534057617, 20.81999969482422, 20.84000015258789, 21.149999618530273,
      21.5, 21.059999465942383, 20.790000915527344, 20.299999237060547, 20.420000076293945, 20.700000762939453,
      21.34000015258789, 21.0, 20.8799991607666, 21.649999618530273, 22.0, 22.290000915527344, 22.299999237060547,
      21.469999313354492, 21.889999389648438, 21.639999389648438, 21.100000381469727, 21.579999923706055, 21.0,
      20.43000030517578, 22.1200008392334, 22.950000762939453, 23.829999923706055, 24.149999618530273, 23.90999984741211,
      24.18000030517578, 24.200000762939453, 24.34000015258789, 24.059999465942383, 24.799999237060547, 25.1299991607666,
      24.899999618530273, 25.31999969482422, 25.93000030517578, 25.68000030517578, 25.280000686645508, 25.290000915527344,
      24.889999389648438, 25.600000381469727, 25.100000381469727, 25.440000534057617, 25.969999313354492, 26.049999237060547,
      25.5, 25.989999771118164, 25.899999618530273, 25.290000915527344, 25.15999984741211, 24.90999984741211, 24.350000381469727,
      25.170000076293945, 25.329999923706055, 25.299999237060547, 24.809999465942383, 25.0, 24.450000762939453, 25.200000762939453,
      24.229999542236328, 25.5, 25.3799991607666, 25.0, 24.93000030517578, 25.0, 24.549999237060547, 24.040000915527344, 23.450000762939453,
      23.149999618530273, 22.829999923706055, 23.0, 23.06999969482422, 22.8700008392334, 23.530000686645508, 23.0, 22.950000762939453,
      22.270000457763672, 22.350000381469727, 22.6200008392334, 21.780000686645508, 23.200000762939453, 22.899999618530273, 23.170000076293945,
      23.31999969482422, 23.479999542236328, 22.59000015258789, 22.149999618530273, 22.309999465942383, 22.899999618530273, 22.100000381469727,
      22.299999237060547, 22.290000915527344, 23.5, 24.299999237060547, 24.200000762939453, 23.799999237060547, 23.649999618530273, 23.709999084472656,
      24.1200008392334, 24.270000457763672, 24.8799991607666, 24.670000076293945, 24.600000381469727, 23.920000076293945, 24.56999969482422,
      24.09000015258789, 24.56999969482422, 24.18000030517578, 24.799999237060547, 24.799999237060547, 24.020000457763672, 23.940000534057617,
      24.440000534057617, 24.610000610351562, 24.81999969482422, 25.030000686645508, 24.81999969482422, 24.649999618530273, 25.020000457763672,
      24.989999771118164, 25.75, 25.149999618530273, 25.200000762939453, 25.600000381469727, 24.850000381469727, 25.18000030517578, 25.549999237060547,
      25.75, 25.440000534057617, 24.729999542236328)
  }

}
