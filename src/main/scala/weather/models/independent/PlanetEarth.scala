package weather.models.independent

import squants.space.Angle
import squants.space.Degrees

/** @see https://en.wikipedia.org/wiki/Axial_tilt */
final case class PlanetEarth(axialTilt: Angle)

object PlanetEarth {

  /**
   * Currently, the Earth tilt is not modelled.
   * Thus the offset is always 23.43715°.
   */
  object Constant {

    @SuppressWarnings(Array("UnusedMethodParameter"))
    def forDate(when: java.time.ZonedDateTime): Option[PlanetEarth] =
      Some(PlanetEarth(Degrees(23.43715)))

  }

} // end of PlanetEarth

// vim: set ts=2 sw=2 et:
