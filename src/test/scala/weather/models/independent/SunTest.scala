package weather.models.independent

import org.scalatest._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

class SunTest extends FlatSpec with Matchers {

  "Sun.apply" should "construct" in {
    Sun(-9).intensityOffset shouldBe -9
    Sun(0).intensityOffset shouldBe 0
    Sun(99).intensityOffset shouldBe 99
  }

} // end of SunTest

object SunCheck extends Properties("Sun") {

  property("Sun.apply") = forAll { d: Double =>
    Sun(d).intensityOffset == d
  }

  property("Sun.Constant.forDate") = forAll { i: Int =>
    import java.time._
    val d = ZonedDateTime.ofInstant(Instant.ofEpochSecond(i), ZoneId.systemDefault)
    Sun.Constant.forDate(d).map(_.intensityOffset) == Some(0)
  }

} // end of SunCheck

// vim: set ts=2 sw=2 et:
