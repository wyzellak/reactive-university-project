package controllers

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
    val companyNames = QuotationService.getCompanyNames

      companyNames.map { companyNames =>
        Ok(views.html.quotations(QuotationForm.form, IndexForm.form, Seq.empty[Quotation], companyNames))
      }

  }

  def addQuotation() = Action.async { implicit request =>
    val companyNames = QuotationService.getCompanyNames

    companyNames.flatMap { names =>
      QuotationForm.form.bindFromRequest.fold(
        errorForm => Future.successful(Ok(views.html.quotations(errorForm, IndexForm.form, Seq.empty[Quotation], names))),
        data => {
          val newQuotation = Quotation(0, data.company_name, data.opening, data.max, data.min, data.closing, data.change_percentage, data.volume, data.date)
          QuotationService.addQuotation(newQuotation).map(res =>
            Redirect(routes.QuotationsController.index())
          )
        })
    }
  }

  def deleteQuotation(id: Int) = Action.async { implicit request =>
    QuotationService.deleteQuotation(id) map { res =>
      Redirect(routes.QuotationsController.index())
    }
  }

  def displayQuotations(name: String) = Action.async { implicit request =>
    val quotationsForCompany = QuotationService.getByCompanyName(name)
    val companyNames = QuotationService.getCompanyNames

    quotationsForCompany.flatMap { quotations =>
      companyNames.map { companyNames =>
        Ok(views.html.quotations(QuotationForm.form, IndexForm.form, quotations, companyNames))
      }
    }
  }

  def runIndex() = Action.async { implicit request =>
    val companyNames = QuotationService.getCompanyNames

    companyNames.flatMap { names =>
      IndexForm.form.bindFromRequest.fold(
        errorForm => Future.successful(Ok(views.html.quotations(QuotationForm.form, errorForm, Seq.empty[Quotation], names))),
        data => {
          IndexService.runIndex(data.indexName, data.companyName).map(res =>
            Redirect(routes.QuotationsController.index())
          )
        })
    }
  }

}
