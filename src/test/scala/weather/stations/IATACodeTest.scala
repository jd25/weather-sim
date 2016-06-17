package weather.stations

import org.scalatest._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import weather.stations.IATACode._
import weather.units.Angles._
import weather.units.AnglesCheck._
import weather.units.Distances._
import weather.units.DistancesCheck._

class IATACodeTest extends FlatSpec with Matchers {

  "IATACode.predefined" should "contain enough examples" in {
    IATACode.predefined.toSet.size >= 10
  }

  "IATACode.ofLocation" should "be Some for predefined locations" in {
    val PER = IATACode.ofLocation("PER")
    PER.isDefined && PER.forall(_.id == "PER")
  }

  "IATACode.ofLocation" should "be None for invalid locations" in {
    val PER = IATACode.ofLocation("\n")
    PER.isEmpty
  }

} // end of IATACodeTest

object IATACodeCheck extends Properties("IATACode") {

  property("IATACode.matches") = forAll { (id: String, lat: Latitude, lon: Longitude, h: Height) =>
    val ic = IATACode(id, lat, lon, h)
    ic.matches(id) && ic.matches(lat, lon)
  }

} // end of IATACodeCheck

// vim: set ts=2 sw=2 et:
