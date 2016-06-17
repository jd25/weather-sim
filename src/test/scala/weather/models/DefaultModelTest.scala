package weather.models

import org.scalatest._

import org.scalacheck.Properties

import java.time.Duration
import java.time.ZonedDateTime

import squants.space.Degrees
import squants.thermal.Celsius

import weather.units.Angles._
import weather.models.DefaultBehaviour.IndependentLogic._
import weather.models.DefaultBehaviour.DependentLogic._

class DefaultBehaviourTest extends FlatSpec with Matchers {

  val temp1 = Celsius(10)
  val temp2 = Celsius(20)
  val temp3 = Celsius(30)
  val hour = Duration.ofHours(1)
  val gmt = Longitude(Degrees(0))
  val equator = Latitude(Degrees(0))
  val seconds = (0 to SECONDS_PER_DAY by 3600)
  val dayCycle: Seq[Double] = seconds.map(middayMidnightCosine(_))
  val startOfDay = ZonedDateTime.now.truncatedTo(java.time.temporal.ChronoUnit.DAYS)

  "DefaultBehaviour.IndependentLogic.sunshineTemperatureFactor" should "be strong at the equator" in {
    val temps = dayCycle.map(cos => sunshineTemperatureFactor(temp2, equator, cos))
    temps.min should be < (temp2)
    temps.max should be > (temp2)
    temps.head should be(temps.last)
    val midday = seconds.size / 2
    temps(midday) should be > (temps.max * 0.8)
    temps.head should be < temps(midday)
  }

  "DefaultBehaviour.IndependentLogic.sunshineTemperatureFactor" should "be weak at the poles" in {
    val lat1 = Latitude(Degrees(MaxLat))
    val lat2 = Latitude(Degrees(-MaxLat))
    val pole1 = dayCycle.map(cos => sunshineTemperatureFactor(temp2, lat1, cos))
    pole1.forall(_ == temp2) should be(true)
    val pole2 = dayCycle.map(cos => sunshineTemperatureFactor(temp2, lat2, cos))
    pole2.forall(_ == temp2) should be(true)
  }

  "DefaultBehaviour.IndependentLogic.middayMidnightCosine" should "cycle cold to hot to cold" in {
    // midnight
    dayCycle.head should (be >= -1.0 and be < -0.9)
    dayCycle.last should equal(dayCycle.head)

    // midday
    val midday = seconds.size / 2
    dayCycle(midday) should (be > 0.9 and be <= 1.0)
  }

  "DefaultBehaviour.IndependentLogic.updatedSunshine" should "cycle at the equator" in {
    val logic = Seq(updatedSunshine, updatedIndependentTime)

    val cell = Cell(equator, gmt, timestamp = startOfDay, temperature = temp1)
    val start = cell.timestamp.toLocalTime.toSecondOfDay
    val intervals = (0 to 24).map(hour multipliedBy _)

    // leaping from the base case
    val jumped = intervals.map {
      case hours =>
        Simulator.applyIndependentLogic(logic, hours)(cell)
    }

    jumped.map(_.temperature).min should be <= (cell.temperature)
    jumped.map(_.temperature).min should be > (cell.temperature * 0.8)
    jumped.map(_.temperature).max should be > (cell.temperature * 1.2)
    jumped.map(_.temperature).max should be < (cell.temperature * 3)

    // stepping one hour at a time
    val stepped = intervals.scanLeft(cell) {
      case (prev, hourOfDay) =>
        Simulator.applyIndependentLogic(logic, hour)(prev)
    }

    stepped.map(_.temperature).min should be < (cell.temperature) // @todo fix divergence
    stepped.map(_.temperature).min should be > (cell.temperature * 0.8)
    stepped.map(_.temperature).max should be > (cell.temperature * 1.2)
    stepped.map(_.temperature).max should be < (cell.temperature * 3)
  }

  "DefaultBehaviour.DependentLogic.updatedCondition" should "make sense" in {
    import weather.models.dependent.Condition._
    val cell = Cell(equator, gmt, timestamp = startOfDay, temperature = temp1)

    def test(cell: Cell) = updatedCondition(cell, hour, GridHistory.empty, Grid.empty).condition
    test(cell) should be(Mild)

    val temperatures = Map(
      -20 -> Snow, -10 -> Snow,
      -5 -> Cold, 0 -> Cold, 5 -> Cold,
      10 -> Mild, 15 -> Mild,
      20 -> Sunny, 25 -> Sunny,
      30 -> Hot, 35 -> Hot
    )
    temperatures.foreach {
      case (temp, cond) =>
        test(cell.copy(temperature = Celsius(temp))) should be(cond)
    }

    val humidity = Map(
      0 -> Dry,
      20 -> Dry,
      30 -> Mild,
      40 -> Mild,
      50 -> Mild,
      60 -> Cloudy,
      80 -> Humid,
      100 -> Rain
    )
    humidity.foreach {
      case (rh, cond) =>
        test(cell.copy(humidity = rh)) should be(cond)
    }

  }

  "DefaultBehaviour.applyTopography" should "update elevations" in {
    val top = Latitude(Degrees(45))
    val bottom = Latitude(Degrees(-45))
    val left = Longitude(Degrees(-90))
    val right = Longitude(Degrees(90))
    val grid = Grid.uniform(top, left, bottom, right, 10, startOfDay)
    val topo = grid.map(DefaultBehaviour.applyTopography)
    grid.get.cells.head.head.elevation.length.toMeters should be(0)
    topo.get.cells.head.head.elevation.length.toMeters should not be (0)
  }

} // end of DefaultBehaviourTest

// vim: set ts=2 sw=2 et:
