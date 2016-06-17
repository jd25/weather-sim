package weather.units

import org.scalatest._

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Properties
import org.scalacheck.Prop._

import squants.space.Meters

import weather.units.Distances._

object DistancesCheck extends Properties("Distances") {

  implicit val genHeight = Arbitrary(Gen.choose(-1000, 1000).map(Height.ofMetres(_)))

  property("Height.ofMetres") = forAll { h: Int =>
    Height.ofMetres(h) match {
      case Height(dist) if dist == Meters(h) => true
      case _ => false
    }
  }

} // end of DistancesCheck

// vim: set ts=2 sw=2 et:
