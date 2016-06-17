package weather

import java.time.Duration

import squants.space.Angle
import squants.space.Degrees

import weather.io.TaskOutput
import weather.models._
import weather.models.DefaultBehaviour._
import weather.stations._
import weather.units.Angles._

/**
 * A hard-coded demo for the Weather Simulator, which outputs the
 * results to a file. For ‘realism’ some random jitter is added to the
 * time between readings, which effectively adds randomness to the
 * simulation results. In amongst the text UI, the actually running of
 * the simulation is concisely done by creating a lazy stream as noted
 * in comments below.
 */
object Demo {
  // scalastyle:off

  /** Command-line interface. */

  def main(args: Array[String]): Unit = args match {
    case Array(outFileName) => runDemo(outFileName)
    case _ => System.err.println("Usage: weather.models <output.txt>")
  }

  // ****** Setup ******

  // Hard-coded demo configuration to save time

  val gridDimension = 120 // similar to elevation.bmp
  val simulationDuration = Duration.ofDays(7)
  val timeInterval = Duration.ofHours(24 / 4)
  val readingsPerDay = 2.2
  val locations = IATACode.predefined
  val stations = locations.map(WeatherStation(_))
  /** Simulates failures and embodies the non-uniform time between readings. */
  val failureRate = 1.0 / readingsPerDay
  val duplicationRate = 0.02

  /**
   * Expand the simulation border a bit to avoid edge effects when
   * simulating a small area. Minimum ~500 km^2.
   */
  val expandGridBoundaryDegrees = 2.0

  // ****** UI - displaying stats to the user ******

  def runDemo(outFileName: String): Unit = {
    println(s"Project $BuildInfo")

    for {
      now <- Some(java.time.ZonedDateTime.now)
      // continental boundary of monitoring stations
      top = addLat(locations.map(_.latitude).maxBy(_.angle), expandGridBoundaryDegrees)
      left = addLon(locations.map(_.longitude).minBy(_.angle), -expandGridBoundaryDegrees)
      bottom = addLat(locations.map(_.latitude).minBy(_.angle), -expandGridBoundaryDegrees)
      right = addLon(locations.map(_.longitude).maxBy(_.angle), expandGridBoundaryDegrees)
      // simulator
      grid <- Grid.uniform(top, left, bottom, right, gridDimension, now)
        .map(DefaultBehaviour.applyTopography)
      iterations = simulationDuration.getSeconds / timeInterval.getSeconds
    } {
      // display quick-and-dirty stats
      val spatialAngle = grid.cells(0).head.latitude.angle - grid.cells(1).head.latitude.angle
      val vertical = latToKm(top.angle - bottom.angle)
      val horizontal = lonToKm(right.angle - left.angle, (top.angle - bottom.angle) / 2)

      println
      println(s"Simulation size: $vertical x $horizontal km")
      println(s"Grid cells: ${grid.cells.size} lat x ${grid.cells.head.size} lon (${grid.cells.size * grid.cells.head.size})")
      println(s"Spatial resolution: $spatialAngle (${latToKm(spatialAngle)} x ${lonToKm(spatialAngle, Degrees(0))} km)")
      println(s"Average time interval (ISO-8601): $timeInterval ($iterations iterations of the simulation)")
      println(s"""Weather monitoring stations (${stations.size}): ${locations.map(_.id).sorted.mkString(", ")}""")
      println(s"Simulation duration: ${simulationDuration.toDays} days")
      println(f"Station readings per day: $readingsPerDay (failure rate $failureRate%.2f)")
      println(s"Nominal readings: ${Math.round(readingsPerDay * simulationDuration.toDays * stations.size)}")

      // Add a bit of jitter to the timeInterval of the readings
      val timeJitter = new util.Random(0)
      def nextTimeInterval =
        java.time.Duration.ofSeconds((timeInterval.getSeconds * 1 + (timeJitter.nextFloat - 0.5) * 3600).toLong)

      // ****** ‘run’ the simulation (lazy stream) ******
      val source = Weather.ofGrid(grid).toStream(nextTimeInterval).take(iterations.toInt) // jitter
      val stream = simulateStationReadings(source, stations) // duplications / errors / omissions

      // ****** output the results ******
      runReadingsWriter(outFileName, TaskOutput.header #:: stream) // file I/O
      println
      println(s"Output written to $outFileName")
    }

  }

  // ****** Simulate Errors ******

  protected val chaosMonkey = new util.Random(0)

  // evolution
  protected def simulateStationReadings(source: Stream[Weather], stations: Seq[WeatherStation]): Stream[String] = {
    for (weather <- source; station <- stations; reading <- simulateStationReading(weather, station)) yield reading
  }

  // temperamental stations
  protected def simulateStationReading(weather: Weather, station: WeatherStation): Seq[String] = {
    val id = station.iataCode.id
    val repeats = if (chaosMonkey.nextFloat < duplicationRate) chaosMonkey.nextInt(3) else 0
    if (repeats > 0) println(s"Repeated measurements at $id ($repeats)")
    for {
      _ <- (0 to repeats)
      failure = chaosMonkey.nextFloat < failureRate if !failure
      cell <- weather.ofStation(station) // cell closest to the station (no interpolation)
    } yield {
      // sanity check that the cell is close to the station
      assume(latToKm(station.iataCode.latitude.angle - cell.latitude.angle) < 100)
      assume(lonToKm(station.iataCode.longitude.angle - cell.longitude.angle, cell.latitude.angle) < 100)
      TaskOutput.format(station, cell)
    }
  }

  // ****** Utils ******

  // output
  protected def runReadingsWriter(filename: String, lines: Stream[String]): Unit = {
    val writer = new java.io.FileWriter(filename)
    try {
      var count = 0
      def lineWriter(line: String) = {
        writer.write(line)
        count = count + 1
      }
      runLineWriter(lineWriter)(lines)
      println(s"Actual readings: $count")
    } finally {
      writer.close
    }
  }

  protected def runLineWriter(write: String => Unit)(lines: Seq[String]): Unit = {
    // lines.foreach(println)
    lines.foreach(line => write(line + "\n"))
  }

  /** @todo make an tested API for Latitude and Longitude operations. */
  protected def addLat(latitude: Latitude, amount: Double) =
    Latitude(latitude.angle + Degrees(amount))

  protected def addLon(longitude: Longitude, amount: Double) =
    Longitude(longitude.angle + Degrees(amount))

  // The following display functions are so quick and dirty that I didn’t
  // even add them to the Latitude/Longitude API.

  protected def latToKm(latitude: Angle) =
    Math.round(latitude.toDegrees * 110.574)

  protected def lonToKm(longitude: Angle, latitude: Angle) =
    Math.round(longitude.toDegrees * Math.cos(latitude.toRadians) * 111.320)

  protected def displayLatitudeKm(latitude: Angle): String = {
    val km = latToKm(latitude)
    if (km < 0) s"$km S" else s"$km N"
  }

  protected def displayLongitudeKm(longitude: Angle, latitude: Angle): String = {
    val km = lonToKm(longitude, latitude)
    if (km < 0) s"$km E" else s"$km W"
  }

} // end of Demo

// vim: set ts=2 sw=2 et:
