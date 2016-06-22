package actors.stock
import java.util.concurrent.ThreadLocalRandom

import actors.{StockIndexAlgorithms}
import akka.actor.{ActorSystem, Props}
import akka.actor.Status.Success
import akka.testkit.TestActorRef
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import services.QuotationService


class StocksIndexAlgorithmsTests extends FeatureSpec with GivenWhenThen {

  info("We can put info later here")

  feature("Calculating Moving Average") {
    scenario("As a user I want to obtain weighted average results for period from to with everyday frequency update ") {

      Given("an actor for weighted average is created and we have input values defined")
      implicit val system = ActorSystem()
      val actorRef = TestActorRef[StockIndexAlgorithms]
//      val random = new scala.util.Random()
      val dummyInputList = List(Stream.continually(ThreadLocalRandom.current().nextDouble(0, 100)).take(500)).flatten

      When("calculate weighted average")
      val results = actorRef.underlyingActor.calculateWeightedMovingAverage(dummyInputList)
      val results2 = actorRef.underlyingActor.calculateMovingAveragesForIndex(dummyInputList)
//      actorRef.underlyingActor.calculateMovingAveragesOnActorSystem(QuotationService.listAllQuotations, new java.util.Date(), new java.util.Date())
      Then("Results should be:")
      assert(results === 2)
    }

  }
}

