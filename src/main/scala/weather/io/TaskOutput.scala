package weather.io

import java.time.ZonedDateTime

import squants.motion.Pressure
import squants.thermal.Temperature

import weather.models.Cell
import weather.stations.WeatherStation

/** Formatting for the Task specification. */
object TaskOutput {

  val interFieldDelimiter = "|"
  val intraFieldDelimiter = ","

  val header = Seq(
    "Station",
    "Location",
    "Timestamp",
    "Condition",
    "Celsius",
    "hPa",
    "RelHumidity"
  ).mkString(interFieldDelimiter)

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.ToString"))
  def format(station: WeatherStation, cell: Cell): String = {
    val fields = Seq(
      station.iataCode.id,
      formatStationLocation(station),
      formatTimestamp(cell.timestamp),
      cell.condition.toString,
      formatTemperatureCelcius(cell.temperature),
      formatPressureHectopascals(cell.pressure),
      formatHumidityPercent(cell.humidity)
    )
    fields.mkString(interFieldDelimiter)
  }

  def formatStationLocation(station: WeatherStation): String = {
    val lat = station.iataCode.latitude.angle.toDegrees.toString
    val lon = station.iataCode.longitude.angle.toDegrees.toString
    val alt = station.iataCode.elevation.length.toMeters.toInt.toString
    Seq(lat, lon, alt).mkString(intraFieldDelimiter)
  }

  def formatTimestamp(timestamp: ZonedDateTime): String = {
    val simplified = timestamp.truncatedTo(java.time.temporal.ChronoUnit.SECONDS)
    simplified.format(java.time.format.DateTimeFormatter.ISO_INSTANT)
  }

  def formatTemperatureCelcius(temperature: Temperature): String = {
    f"${temperature.toCelsiusDegrees}%+.1f"
  }

  def formatPressureHectopascals(pressure: Pressure): String = {
    f"${pressure.toPascals / 100.0}%.1f"
  }

  def formatHumidityPercent(percent: Int): String = {
    percent.toString
  }

} // end of TaskOutput

// vim: set ts=2 sw=2 et:
