package controllers

import java.util.Date

import model.{Quotation, QuotationForm}
import play.api.mvc.Action
import services.QuotationService
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
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
        Ok(views.html.quotations(QuotationForm.form, quotationsForAlior, companyNames))
      }
    }

  }

  def addQuotation() = Action.async { implicit request =>
    QuotationForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.quotations(errorForm, Seq.empty[Quotation], Seq.empty[String]))),
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
}
