package weather.units

import org.scalatest._

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Properties
import org.scalacheck.Prop._

import squants.space.Degrees

import weather.units.Angles._

class AnglesTest extends FlatSpec with Matchers {

} // end of AnglesTest

object AnglesCheck extends Properties("Angles") {

  implicit val genDegrees = Gen.choose(-360, 360)

  implicit val genLatitude = Arbitrary(Gen.choose(-MaxLat, MaxLat - 1).map(Degrees(_)).map(Latitude(_)))

  implicit val genLongitude = Arbitrary(Gen.choose(-MaxLon, MaxLon - 1).map(Degrees(_)).map(Longitude(_)))

  property("Angles.Latitude") = forAll(genDegrees) { lat: Int =>
    classify(Math.abs(lat) <= MaxLat, "in range", "out of range") {
      Latitude.ofDegrees(lat) match {
        case None if lat <= -MaxLat || lat >= MaxLat => true
        case Some(_) if lat >= -MaxLat && lat <= MaxLat => true
        case _ => false
      }
    }
  }

  property("Angles.Longitude") = forAll(genDegrees) { lon: Int =>
    classify(Math.abs(lon) <= MaxLon, "in range", "out of range") {
      Longitude.ofDegrees(lon) match {
        case None if lon < -MaxLon || lon >= MaxLon => true
        case Some(_) if lon >= -MaxLon && lon < MaxLon => true
        case _ => false
      }
    }
  }

} // end of AnglesCheck

// vim: set ts=2 sw=2 et:
