package weather.models

import java.time.Duration

import org.scalatest._

import org.scalacheck.Properties

import squants.space.Degrees

import weather.models.DefaultBehaviour._
import weather.units.Angles._

class WeatherTest extends FlatSpec with Matchers {

  val test1 = new GridTest()
  val grid1 = Grid.uniform(test1.latTop, test1.lonLeft, test1.latBottom, test1.lonRight, 4, test1.now)
  val weather1 = Weather.ofGrid(grid1.get)

  "Weather.ofGrid" should "work" in {

    weather1.grid should be(grid1.get)
    weather1.history.cells.map(_.map(_.head)) should equal(grid1.get.cells)

  }

  /** @todo set up proper integration tests. */
  "Weather and DefaultBehaviour" should "produce some sane deterministic results for 7 days" in {
    val station = new weather.stations.WeatherStationTest().PER.get
    val spread = Degrees(20)
    val top = Latitude(station.iataCode.latitude.angle + spread)
    val bottom = Latitude(station.iataCode.latitude.angle - spread)
    val left = Longitude(station.iataCode.longitude.angle - spread)
    val right = Longitude(station.iataCode.longitude.angle + spread)
    val simulationDuration = Duration.ofDays(7)
    val timeInterval = Duration.ofHours(24 / 4)
    val iterations = (simulationDuration.getSeconds / timeInterval.getSeconds).toInt
    val time = java.time.ZonedDateTime.of(2016, 6, 15, 12, 30, 30, 0, java.time.ZoneId.of("Australia/Perth"))
    val grid = Grid.uniform(top, left, bottom, right, cellsOnLongSide = 100, time)
      .map(DefaultBehaviour.applyTopography).get
    val weathers = Weather.ofGrid(grid).toStream(timeInterval).take(iterations.toInt)
    val readings = weathers.flatMap(weather => weather.ofStation(station))

    readings.size should be(iterations)

    val temperatures = readings.map(_.temperature.toCelsiusDegrees)
    val pressures = readings.map(_.pressure.toPascals)
    val humidities = readings.map(_.humidity)

    temperatures.toSet.size should be > (iterations / 2)
    temperatures.min should be >= (-40.0)
    temperatures.max should be <= (50.0)

    pressures.toSet.size should be > (iterations / 2)
    pressures.min should be >= (50000.0)
    pressures.max should be <= (150000.0)

    humidities.toSet.size should be > (iterations / 2)
    humidities.min should be >= (0)
    humidities.max should be <= (100)

    val readingsRepeated1 = weathers.flatMap(weather => weather.ofStation(station))
    readings should equal(readingsRepeated1)

    val readingsRepeated2 = weathers.flatMap(weather => weather.ofStation(station))
    readings should equal(readingsRepeated2)
  }

} // end of WeatherTest

object WeatherCheck extends Properties("Weather") {

  // @todo ofGrid

} // end of WeatherCheck

// vim: set ts=2 sw=2 et:
