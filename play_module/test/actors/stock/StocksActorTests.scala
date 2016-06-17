package actors.stock
import actors.StocksActor
import akka.actor.{ActorSystem, Props}
import akka.actor.Status.Success
import akka.testkit.TestActorRef
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen


class StocksActorTests extends FeatureSpec with GivenWhenThen {

info("we can put info later here")

  feature("Calculating Moving Average") {
    scenario("As a user I want to obtain weighted average results for period from to with everyday frequency update ") {

      Given("an actor for weighted average is created and we have input values defined")
      implicit val system = ActorSystem()
      val actorRef = TestActorRef[StocksActor]
      val random = new scala.util.Random()
      val dummyInputList = List(Stream.continually(random.nextDouble()).take(500)).flatten

      When("calculate weighted average")
      val results = actorRef.underlyingActor.calculateWeightedMovingAverage(dummyInputList)

      Then("Results should be:")
      assert(results === 2)
    }

  }
}

