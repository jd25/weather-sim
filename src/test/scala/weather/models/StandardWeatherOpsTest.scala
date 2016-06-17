package weather.models

import java.time.Duration.ZERO

import org.scalatest._

import org.scalacheck.Properties

import weather.models.DefaultBehaviour._
import weather.stations._

class StandardWeatherOpsTest extends FlatSpec with Matchers {

  val test1 = new WeatherTest()

  "StandardWeatherOps.toStream(ZERO)" should "do nothing" in {

    val stream = test1.weather1.toStream(ZERO)

    stream.nonEmpty should be(true)
    stream.head should equal(test1.weather1)
    stream.drop(1).head should equal(test1.weather1)

  }

  "StandardWeatherOps.toStream(-ve)" should "do nothing" in {

    val invalid = java.time.Duration.ofDays(-1)

    val stream = test1.weather1.toStream(invalid)

    stream.nonEmpty should be(true)
    stream.head should equal(test1.weather1)
    stream.drop(1).head should equal(test1.weather1)

  }

  "StandardWeatherOps.toIterator(ZERO)" should "do nothing" in {

    val iterator = test1.weather1.toIterator(ZERO)

    iterator.hasNext should be(true)
    iterator.next should equal(test1.weather1)
    iterator.next should equal(test1.weather1)

  }

  "StandardWeatherOps.toIterator(+ve)" should "do something" in {

    val valid = java.time.Duration.ofDays(1)

    val iterator = test1.weather1.toIterator(valid)

    iterator.hasNext should be(true)
    iterator.next should not equal (test1.weather1)
    iterator.next should not equal (test1.weather1)

    val prev = test1.weather1.grid.cells.head.head.timestamp
    val next = iterator.next.grid.cells.head.head.timestamp
    (next.toEpochSecond - prev.toEpochSecond) should equal(valid.getSeconds * 3)

  }

  val now = java.time.ZonedDateTime.now
  val PER = IATACode.ofLocation("PER").get
  val SYD = IATACode.ofLocation("SYD").get
  val ADL = IATACode.ofLocation("ADL").get
  val PHE = IATACode.ofLocation("PHE").get
  val grid = Grid.uniform(PER.latitude, PER.longitude, SYD.latitude, SYD.longitude, 10, now).get
  val weather = Weather.ofGrid(grid)

  "StandardWeatherOps.ofStation" should "return a cell if possible" in {
    // @todo check that the cells are at the right latitude & longitude
    weather.ofStation(WeatherStation(PER)) should not be (None)
    weather.ofStation(WeatherStation(SYD)) should not be (None)
    weather.ofStation(WeatherStation(ADL)) should not be (None)
  }

  "StandardWeatherOps.ofStation" should "not return a cell if not possible" in {
    weather.ofStation(WeatherStation(PHE)) should be(None)
  }

} // end of StandardWeatherOpsTest

object StandardWeatherOpsCheck extends Properties("StandardWeatherOps") {

} // end of StandardWeatherOpsCheck

// vim: set ts=2 sw=2 et:
