package weather.stations

import org.scalatest._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

import weather.stations.WeatherStation._
import weather.units.Angles._
import weather.units.AnglesCheck._
import weather.units.Distances._
import weather.units.DistancesCheck._

class WeatherStationTest extends FlatSpec with Matchers {

  val PER = IATACode.ofLocation("PER").map(WeatherStation(_))
  assume(PER.isDefined)

  val SYD = IATACode.ofLocation("SYD").map(WeatherStation(_))
  assume(SYD.isDefined)

  "WeatherStation.predefined" should "recognise codes" in {
    WeatherStation.predefined("PER") should be(PER)
    WeatherStation.predefined("SYD") should be(SYD)
    WeatherStation.predefined("_X_") should be(None)
  }

  "WeatherStation.predefined" should "recognise latitude & longitude" in {
    WeatherStation.predefined(PER.get.iataCode.latitude, PER.get.iataCode.longitude) should be(PER)
    WeatherStation.predefined(SYD.get.iataCode.latitude, SYD.get.iataCode.longitude) should be(SYD)
    WeatherStation.predefined(PER.get.iataCode.latitude, SYD.get.iataCode.longitude) should be(None)
  }

} // end of WeatherStationTest

object WeatherStationCheck extends Properties("WeatherStation") {

} // end of WeatherStationCheck

// vim: set ts=2 sw=2 et:
