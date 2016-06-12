package services

import model.{Quotation, Quotations}

import scala.concurrent.Future

/**
  * @author alisowsk
  */
object IndexService {

  def runIndex(indexName: String, companyNames: Seq[String]): Future[Int] = {
    // TODO
    Future.successful(1)
  }

  private def calculateAverageTrueRange() = {

  }

  private def calculateEaseOfMovement() = {

  }

  private def calculateMovingAverage() = {

  }
//  def addQuotation(quotation: Quotation): Future[String] = {
//    Quotations.add(quotation)
//  }
//
//  def deleteQuotation(id: Int): Future[Int] = {
//    Quotations.delete(id)
//  }
//
//  def getQuotation(id: Int): Future[Option[Quotation]] = {
//    Quotations.get(id)
//  }
//
//  def getByCompanyName(companyName: String): Future[Seq[Quotation]] = {
//    Quotations.getByCompanyName(companyName)
//  }
//
//  def listAllQuotations: Future[Seq[Quotation]] = {
//    Quotations.listAll
//  }
//
//  def getCompanyNames: Future[Seq[String]] = {
//    Quotations.getCompanyNames()
//  }
}
