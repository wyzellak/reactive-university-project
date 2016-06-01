package controllers

import model.{Quotation, QuotationForm}
import play.api.mvc.{Action, _}
import services.QuotationService
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * @author alisowsk
  */
class QuotationsController extends Controller {

  def index = Action.async { implicit request =>
    QuotationService.getByCompanyName("ALIOR") map { quotations =>
      Ok(views.html.quotations(QuotationForm.form, quotations))
    }
  }

  def addQuotation() = Action.async { implicit request =>
    QuotationForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.quotations(errorForm, Seq.empty[Quotation]))),
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
