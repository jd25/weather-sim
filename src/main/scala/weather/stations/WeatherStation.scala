package weather.stations

import weather.units.Angles._

/**
 * A WeatherStation can be located at an IATACode and used to obtain
 * results from the weather model.
 * @see Task specification.
 */
final case class WeatherStation(iataCode: IATACode)

object WeatherStation {

  def predefined(iataCode: String): Option[WeatherStation] =
    IATACode.predefined.find(_.matches(iataCode)).map(WeatherStation(_))

  def predefined(latitude: Latitude, longitude: Longitude): Option[WeatherStation] =
    IATACode.predefined.find(_.matches(latitude, longitude)).map(WeatherStation(_))

} // end of WeatherStation

// vim: set ts=2 sw=2 et:
