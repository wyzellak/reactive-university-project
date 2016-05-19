package model

import java.util.Date

import StockPersistenceContext._

/**
  * @author alisowsk
  */
class Quotation(
          var company_name: String,
          var opening: Float,
          var max: Float,
          var min: Float,
          var closing: Float,
          var change_percentage: Float,
          var volume: Integer,
          var date: Date
          ) extends Entity
