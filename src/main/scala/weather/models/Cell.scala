package weather.models

import java.time.ZonedDateTime

import squants.motion.Pressure
import squants.space.Meters
import squants.thermal.Temperature

import weather.models.independent._
import weather.models.dependent._
import weather.units.Angles._
import weather.units.Distances._

/**
 * Cells are weather data, arranged in continguous Grid of squares.
 * The lat and long represent the midpoint of the square.
 * @param condition descriptor like Rain, Snow, Sunny
 * @param elevation height of terrain above sea level (+ve) and distribution of seas and lakes (-ve)
 * @param vegetation foliage (+ve), desert (zero) or ice (-ve)
 * @see [[DefaultBehaviour]] for implicits.
 * @todo add wind vectors, clouds
 */
final case class Cell(
  latitude: Latitude,
  longitude: Longitude,
  timestamp: ZonedDateTime = ZonedDateTime.now,
  // independent environment
  sun: Option[Sun] = Cell.defaultSun,
  earth: Option[PlanetEarth] = Cell.defaultEarth,
  elevation: Height = Cell.defaultElevation,
  vegetation: Int = Cell.defaultVegetation,
  // dependent weather
  condition: Condition = Condition.Unknown,
  temperature: Temperature = Cell.defaultTemperature,
  pressure: Pressure = Cell.defaultPressure,
  humidity: Int = Cell.defaultHumidity
)

object Cell {
  // scalastyle:off magic.number

  /** Assume the simulation starts now, by default. */
  implicit val now: ZonedDateTime = ZonedDateTime.now

  /** Constant. */
  implicit val defaultSun: Option[Sun] = Sun.Constant.forDate(now)

  /** Constant. */
  implicit val defaultEarth: Option[PlanetEarth] = PlanetEarth.Constant.forDate(now)

  /** Sea level == 0. [[DefaultBehaviour]]: terrain >= 0, water < 0. */
  val defaultElevation = Height(Meters(0))

  /** Dry land. [[DefaultBehaviour]]: foliage > 0, desert == 0, ice < 0. */
  val defaultVegetation: Int = 0

  /** 50% relative. */
  val defaultHumidity: Int = 50

  /** 20 degrees Celsius. */
  val defaultTemperature = squants.thermal.Celsius(20)

  /** 1000 hPA */
  val defaultPressure = squants.motion.Pascals(100000)

} // end of Cell

// vim: set ts=2 sw=2 et:
