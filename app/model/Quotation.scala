package model

import java.util.Date

import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data.format.Formats._
import play.api.libs.openid.Errors.AUTH_CANCEL

/**
  * @author alisowsk
  */
case class Quotation(
          id: Int,
          company_name: String,
          opening: Float,
          max: Float,
          min: Float,
          closing: Float,
          change_percentage: Float,
          volume: Int,
          date: Date
          )

case class QuotationFormData(
          company_name: String,
          opening: Float,
          max: Float,
          min: Float,
          closing: Float,
          change_percentage: Float,
          volume: Int,
          date: Date
          )

object QuotationForm {
  val form = Form(
    mapping(
      "company_name" -> nonEmptyText,
      "opening" -> of(floatFormat),
      "max" -> of(floatFormat),
      "min" -> of(floatFormat),
      "closing" -> of(floatFormat),
      "change_percentage" -> of(floatFormat),
      "volume" -> number,
      "date" -> date
    )(QuotationFormData.apply)(QuotationFormData.unapply)
  )
}

class QuotationTableDef(tag: Tag) extends Table[Quotation](tag, "quotation") {

  implicit val JavaUtilDateMapper =
    MappedColumnType .base[java.util.Date, java.sql.Timestamp] (
      d => new java.sql.Timestamp(d.getTime),
      d => new java.util.Date(d.getTime))

  def id = column[Int]("id", O.PrimaryKey,O.AutoInc)
  def company_name = column[String]("company_name")
  def opening = column[Float]("opening")
  def max = column[Float]("max")
  def min = column[Float]("min")
  def closing = column[Float]("closing")
  def change_percentage = column[Float]("change_percentage")
  def volume = column[Int]("volume")
  def date = column[Date]("date")

  override def * =
    (id, company_name, opening, max, min, closing, change_percentage, volume, date) <>(Quotation.tupled, Quotation.unapply)
}

object Quotations {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val quotations = TableQuery[QuotationTableDef]

  def add(quotation: Quotation): Future[String] = {
    dbConfig.db.run(quotations += quotation).map(res => "Quotation successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Int): Future[Int] = {
    dbConfig.db.run(quotations.filter(_.id === id).delete)
  }

  def get(id: Int): Future[Option[Quotation]] = {
    dbConfig.db.run(quotations.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Quotation]] = {
    dbConfig.db.run(quotations.result)
  }

  def getByCompanyName(companyName: String): Future[Seq[Quotation]] = {
    dbConfig.db.run(quotations.filter(_.company_name === companyName).result)
  }

  def getCompanyNames(): Future[Seq[String]] = {
    dbConfig.db.run(quotations.map(_.company_name).distinct.result)
  }

}