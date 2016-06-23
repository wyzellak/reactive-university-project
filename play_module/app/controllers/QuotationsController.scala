package controllers

import model.{IndexForm, IndexName, Quotation, QuotationForm}
import play.api.mvc.{Action, _}
import services.{IndexService, QuotationService}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
  * @author alisowsk
  */
class QuotationsController extends Controller {

  def index(name: String) = Action.async { implicit request =>
    val quotationsForCompany = QuotationService.getByCompanyName(name)
    val companyNames = QuotationService.getCompanyNames

    quotationsForCompany.flatMap { quotations =>
      companyNames.map { companyNames =>
        Ok(views.html.quotations(QuotationForm.form, IndexForm.form, quotations, companyNames, 0))
      }
    }
  }

  def addQuotation() = Action.async { implicit request =>
    val companyNames = QuotationService.getCompanyNames

    companyNames.flatMap { names =>
      QuotationForm.form.bindFromRequest.fold(
        errorForm => Future.successful(Ok(views.html.quotations(errorForm, IndexForm.form, Seq.empty[Quotation], names, 0))),
        data => {
          val newQuotation = Quotation(0, data.company_name, data.opening, data.max, data.min, data.closing, data.change_percentage, data.volume, data.date)
          QuotationService.addQuotation(newQuotation).map(res =>
            Redirect(routes.QuotationsController.index(data.company_name))
          )
        })
    }
  }

  def deleteQuotation(id: Int) = Action.async { implicit request =>
    val quotation = QuotationService.getQuotation(id)
    QuotationService.deleteQuotation(id)

    quotation.map { q =>
      Redirect(routes.QuotationsController.index(q.get.company_name))
    }
  }

  def runIndex() = Action.async { implicit request =>
    val companyNames = QuotationService.getCompanyNames

    companyNames.flatMap { names =>
      IndexForm.form.bindFromRequest.fold(
        errorForm => Future.successful(Ok(views.html.quotations(QuotationForm.form, errorForm, Seq.empty[Quotation], names, 0))),
        data => {
          IndexService.runIndex(IndexName.withName(data.indexName), data.companyNames, data.start_date, data.end_date).map(res =>
            Redirect(routes.QuotationsController.index(""))
          )
        })
    }
  }

}
