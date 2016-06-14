package services

import model.{Quotation, Quotations}

import scala.concurrent.Future

/**
  * @author alisowsk
  */
object IndexService {

  def runIndex(indexName: String, companyName: String): Future[Int] = {
    // TODO
    Future.successful(1)
  }

  private def calculateTest() = ???

  private def calculateAverageTrueRange() = ???

  private def calculateEaseOfMovement() = ???

  private def calculateMovingAverage() = ???

}
