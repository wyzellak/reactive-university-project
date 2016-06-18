package model

import play.api.data.Form
import play.api.data.Forms._

/**
  * @author alisowsk
  */
case class IndexFormData(
          indexName: String,
          companyNames: Seq[String]
          )

object IndexForm {
  val form = Form(
    mapping(
      "index_name" -> nonEmptyText,
      "company_names" -> seq(nonEmptyText)
    )(IndexFormData.apply)(IndexFormData.unapply)
  )
}