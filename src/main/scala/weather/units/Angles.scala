package weather.units

import squants.space.Angle
import squants.space.Degrees

object Angles {

  val MaxLat = 90
  val MaxLon = 180

  /** @todo operator API for + - above below notAbove notBelow */
  final case class Latitude(angle: Angle) {
    require(Degrees(-MaxLat) <= angle && angle <= Degrees(MaxLat), s"Invalid Latitude, $angle was not in [-$MaxLat°, $MaxLat°]")
  }

  object Latitude {
    def ofDegrees(d: Double): Option[Latitude] =
      scala.util.Try(Latitude(Degrees(d))).toOption
  }

  /** @todo operator API for + - leftOf rightOf notLeftOf notRightOf */
  final case class Longitude(angle: Angle) {
    require(Degrees(-MaxLon) <= angle && angle < Degrees(MaxLon), s"Invalid Longitude, $angle was not in [-$MaxLon°, $MaxLon°)")
  }

  object Longitude {
    def ofDegrees(d: Double): Option[Longitude] =
      scala.util.Try(Longitude(Degrees(d))).toOption
  }

} // end of Angles

// vim: set ts=2 sw=2 et:
