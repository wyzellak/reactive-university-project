package controllers

import java.util.Optional

import model.{IndexForm, Quotation, QuotationForm}
import play.api.mvc.{Action, _}
import services.{IndexService, QuotationService}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * @author alisowsk
  */
class QuotationsController extends Controller {

  def index = Action.async { implicit request =>
    val quotationsForAlior = QuotationService.getByCompanyName("ALIOR")
    val companyNames = QuotationService.getCompanyNames

    quotationsForAlior.flatMap { quotationsForAlior =>
      companyNames.map { companyNames =>
        Ok(views.html.quotations(QuotationForm.form, IndexForm.form, quotationsForAlior, companyNames))
      }
    }

  }

  def addQuotation() = Action.async { implicit request =>
    QuotationForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.quotations(errorForm, IndexForm.form, Seq.empty[Quotation], Seq.empty[String]))),
      data => {
        val newQuotation = Quotation(0, data.company_name, data.opening, data.max, data.min, data.closing, data.change_percentage, data.volume, data.date)
        QuotationService.addQuotation(newQuotation).map(res =>
          Redirect(routes.QuotationsController.index())
        )
      })
  }

  def deleteQuotation(id: Int) = Action.async { implicit request =>
    QuotationService.deleteQuotation(id) map { res =>
      Redirect(routes.QuotationsController.index())
    }
  }

  def displayQuotationsByCompany(companyName: String) = TODO

  def runIndex() = Action.async { implicit request =>
    IndexForm.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok(views.html.quotations(QuotationForm.form, errorForm, Seq.empty[Quotation], Seq.empty[String]))),
      data => {
        IndexService.runIndex("test", Seq.empty).map(res =>
          Redirect(routes.QuotationsController.index())
        )
      })
  }

}
