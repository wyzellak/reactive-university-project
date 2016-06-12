package model

import play.api.data.Form
import play.api.data.Forms._

/**
  * @author alisowsk
  */
case class IndexFormData(
          indexName: String,
          companyName: String
          )

object IndexForm {
  val form = Form(
    mapping(
      "index_name" -> nonEmptyText,
      "company_name" -> nonEmptyText
    )(IndexFormData.apply)(IndexFormData.unapply)
  )
}