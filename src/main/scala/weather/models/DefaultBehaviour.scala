package weather.models

import scala.util.Random

import java.time.Duration

import squants.thermal.Temperature

import weather.units.Angles._
import weather.models.dependent._

/**
 * Default simulation behaviour for [[Weather]].
 * Note this is physically fake, don’t expect actual meteorology!
 * Contains magic numbers.
 * Currently nothing implements GridHistory (local matrix), only the
 * previous state of the self cell is used.
 * @see [[Cell]] for default factors.
 */
@SuppressWarnings(Array("OptionGet", "org.brianmckenna.wartremover.warts.OptionPartial"))
object DefaultBehaviour {

  implicit val defaultRandom: Random = new Random()
  implicit val defaultIndependentLogic: Set[Simulator.IndependentLogic] = IndependentLogic.all
  implicit val defaultDependentLogic: Seq[Simulator.DependentLogic] = DependentLogic.all

  val QUARTER = 0.25
  val THIRD = 0.33
  val HALF = 0.5

  /** @todo use safe dimension types */
  object IndependentLogic {

    val SECONDS_PER_HOUR = 60 * 60

    val SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR

    val identity = (cell: Cell, interval: Duration) => cell

    val updatedIndependentTime = (cell: Cell, interval: Duration) =>
      cell.copy(timestamp = cell.timestamp plusNanos interval.toNanos)

    /**
     * Basic solar irradiance...actually just assuming it’s hottest at
     * midday, coldest at midnight, being cold at the poles and hotest
     * at the equator.
     * @see https://en.wikipedia.org/wiki/Solar_irradiance
     * @todo integrate the change instead of just looking at the endpoints (kludge alert!)
     */
    val updatedSunshine = (cell: Cell, interval: Duration) => {
      val start = cell.timestamp.toLocalTime.toSecondOfDay.toLong
      val end = start + interval.getSeconds
      val dayCosineDelta = middayMidnightCosine(end) - middayMidnightCosine(start)
      cell.copy(temperature = sunshineTemperatureFactor(cell.temperature, cell.latitude, dayCosineDelta))
    }

    protected[weather] def sunshineTemperatureFactor(temperature: Temperature, latitude: Latitude, dayCosine: Double, alpha: Double = THIRD) = {
      val latitudeFactor: Double = (MaxLat - Math.abs(latitude.angle.toDegrees)) / MaxLat
      temperature * (1 + dayCosine * latitudeFactor * alpha)
    }

    /** Multiplication factor -1 (1 am) to 1 (1 pm). */
    protected[weather] def middayMidnightCosine(secondOfDay: Long) = {
      -Math.cos(2 * Math.PI * (secondOfDay - SECONDS_PER_HOUR) / SECONDS_PER_DAY)
    }

    val all: Set[Simulator.IndependentLogic] = Set(
      updatedSunshine,
      // updatedIndependentTime
      identity
    )

  }

  object DependentLogic {

    val identity = (cell: Cell, interval: Duration, history: GridHistory, future: Grid) => cell

    val updatedTime = (cell: Cell, interval: Duration, history: GridHistory, future: Grid) =>
      cell.copy(timestamp = cell.timestamp plusNanos interval.toNanos)

    val updatedHumidity = (cell: Cell, interval: Duration, history: GridHistory, future: Grid) => {
      val humidityFeedback =
        ((cell.pressure.toPascals / 100) - 1000) / 80 +
          (cell.timestamp.toEpochSecond % 5) - 2 // haha!
      val newHumidity = Math.max(0, Math.min(100, cell.humidity + humidityFeedback.toInt))
      cell.copy(humidity = newHumidity)
    }

    val updatedBarometricPressure = (cell: Cell, interval: Duration, history: GridHistory, future: Grid) =>
      cell.condition match {
        case Condition.Unknown =>
          fakeLocalConditionsFromElevation(cell)
        case _ =>
          val pressureFeedback = cell.temperature.toCelsiusDegrees // haha!
          cell.copy(pressure = cell.pressure + squants.motion.Pascals(pressureFeedback))
      }

    /** Like a randomizer for demos. */
    def fakeLocalConditionsFromElevation(cell: Cell): Cell = {
      val demoVariation = weather.units.Angles.MaxLat - Math.abs(cell.latitude.angle.toDegrees)
      val h = cell.elevation.length.toMeters
      val demoPressure = squants.motion.Pascals((h + demoVariation / 2) * 100)
      val demoTemperature = squants.thermal.Celsius(-h / 4 + (demoVariation / 20))
      val demoHumidity = (h + demoVariation / 10).toInt
      val demoVegetation = h.toInt
      cell.copy(
        humidity = cell.humidity + demoHumidity,
        pressure = cell.pressure + demoPressure,
        temperature = cell.temperature + demoTemperature,
        vegetation = cell.vegetation + demoVegetation
      )
    }

    // scalastyle:off
    /** @todo clear with high pressure, cloudy with low pressure. */
    val updatedCondition = (cell: Cell, interval: Duration, history: GridHistory, future: Grid) => {
      val temp = cell.temperature.toCelsiusDegrees
      val condition: Condition =
        if (temp <= -10) Condition.Snow
        else if (temp < 10) Condition.Cold
        else if (cell.humidity < 30) Condition.Dry
        else if (temp >= 30) Condition.Hot
        else if (cell.humidity > 80) Condition.Rain
        else if (cell.humidity > 60) Condition.Humid
        else if (temp >= 20) Condition.Sunny
        else if (cell.humidity > 50) Condition.Cloudy
        else Condition.Mild
      cell.copy(condition = condition)
    }
    // scalastyle:on

    val all: Seq[Simulator.DependentLogic] = Seq(
      updatedHumidity,
      updatedBarometricPressure,
      updatedCondition,
      updatedTime
    )

  }

  def applyTopography(grid: Grid): Grid = weather.io.ElevationInput.applyDefault(grid)

} // end of DefaultBehaviour

// vim: set ts=2 sw=2 et:
