package weather

import java.time.Duration

import weather.stations.WeatherStation

package object models {

  implicit class WeatherOps(val weather: Weather) extends AnyVal {

    /* Obtain the weather cell (reading) closests to the given station. */
    def ofStation(station: WeatherStation): Option[Cell] =
      weather.grid.lift(station.iataCode.latitude, station.iataCode.longitude)

    /** An infinite stream. */
    def toStream(interval: => Duration): Stream[Weather] = {
      val next = Simulator.simulate(weather, interval)
      next #:: next.toStream(interval)
    }

    /** An infinite iterator. */
    @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Var"))
    def toIterator(interval: => Duration): Iterator[Weather] = new Iterator[Weather] {
      var state: Weather = weather
      def hasNext = true
      def next = { state = Simulator.simulate(state, interval); state }
    }

  } // end of WeatherOps

} // end of package object

// vim: set ts=2 sw=2 et:
