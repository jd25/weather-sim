package weather.units

import squants.space.Length
import squants.space.Meters

object Distances {

  final case class Height(length: Length)

  object Height {

    def ofMetres(h: Double): Height =
      Height(Meters(h))

  }

} // end of Distances

// vim: set ts=2 sw=2 et:
