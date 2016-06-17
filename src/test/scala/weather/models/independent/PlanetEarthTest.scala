package weather.models.independent

import org.scalatest._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import squants.space.Degrees

class PlanetEarthTest extends FlatSpec with Matchers {

  "PlanetEarth.apply" should "construct" in {
    PlanetEarth(Degrees(-9)).axialTilt shouldBe Degrees(-9)
    PlanetEarth(Degrees(0)).axialTilt shouldBe Degrees(0)
    PlanetEarth(Degrees(99)).axialTilt shouldBe Degrees(99)
  }

} // end of PlanetEarthTest

object PlanetEarthCheck extends Properties("PlanetEarth") {

  property("PlanetEarth.apply") = forAll { d: Double =>
    PlanetEarth(Degrees(d)).axialTilt == Degrees(d)
  }

  property("PlanetEarth.Constant.forDate") = forAll { i: Int =>
    import java.time._
    val d = ZonedDateTime.ofInstant(Instant.ofEpochSecond(i), ZoneId.systemDefault)
    PlanetEarth.Constant.forDate(d).map(_.axialTilt) == Some(Degrees(23.43715))
  }

} // end of PlanetEarthCheck

// vim: set ts=2 sw=2 et:
