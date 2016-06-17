package weather.stations

import squants.space.Meters

import weather.units.Angles._
import weather.units.Distances._

/**
 * See [[IATACode]].
 */
final case class IATACode(id: String, latitude: Latitude, longitude: Longitude, elevation: Height) {
  def matches(id: String): Boolean =
    id == this.id

  def matches(latitude: Latitude, longitude: Longitude): Boolean =
    latitude == this.latitude && longitude == this.longitude
}

/**
 * @see http://www.iata.org/publications/Pages/code-search.aspx Airline and Airport Code Search
 * @see http://iatacodes.org IATA Codes Database for Developers
 */
object IATACode {

  // scalastyle:off import.grouping
  import scala.language.implicitConversions
  import squants.space.Degrees
  // scalastyle:on import.grouping

  def ofLocation(code: String): Option[IATACode] =
    IATACode.predefined.find(_.id == code)

  // scalastyle:off magic.number
  val predefinedAustralia = Seq(
    IATACode("ADL", -34.92, 138.62, 48),
    IATACode("BNE", -27.48, 153.04, 8),
    IATACode("CBR", -35.31, 149.20, 577),
    IATACode("DRW", -12.42, 130.89, 30),
    IATACode("HBA", -42.89, 147.33, 50),
    IATACode("KGI", -30.78, 121.45, 365),
    IATACode("MEL", -37.83, 144.98, 7),
    IATACode("PER", -31.92, 115.87, 25),
    IATACode("PHE", -20.37, 118.63, 6),
    IATACode("SYD", -33.86, 151.21, 39)
  )
  // scalastyle:on magic.number

  val predefined = predefinedAustralia

  protected implicit def convertDegreesToLatitude(d: Double): Latitude = Latitude(Degrees(d))

  protected implicit def convertDegreesToLongitude(d: Double): Longitude = Longitude(Degrees(d))

  protected implicit def convertMetresToHeight(h: Int): Height = Height(Meters(h))

} // end of IATACodes

// vim: set ts=2 sw=2 et:
