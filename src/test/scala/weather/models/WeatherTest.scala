package weather.models

import org.scalatest._

import org.scalacheck.Properties

import weather.models.DefaultBehaviour._

class WeatherTest extends FlatSpec with Matchers {

  val test1 = new GridTest()
  val grid1 = Grid.uniform(test1.latTop, test1.lonLeft, test1.latBottom, test1.lonRight, 4, test1.now)
  val weather1 = Weather.ofGrid(grid1.get)

  "Weather.ofGrid" should "work" in {

    weather1.grid should be(grid1.get)
    weather1.history.cells.map(_.map(_.head)) should equal(grid1.get.cells)

  }

} // end of WeatherTest

object WeatherCheck extends Properties("Weather") {

  // @todo ofGrid

} // end of WeatherCheck

// vim: set ts=2 sw=2 et:
