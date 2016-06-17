package weather.io

import java.awt.image.BufferedImage

import squants.space.Meters

import weather.models.Grid
import weather.units.Angles.{ MaxLat, MaxLon }
import weather.units.Angles._
import weather.units.Distances._

/**
 * Read elevation.bmp. This was a low priority task so it works as
 * follows: RGB value (0...255) as metres(!)...obviously fake.
 * @todo modularise...currently it’s all hard-coded.
 * @todo bounds checking etc.
 */
object ElevationInput {

  /** Applies elevation.bmp over the given grid. */
  def applyDefault(grid: Grid): Grid = {
    Grid(grid.cells.map(_.map(cell =>
      cell.copy(elevation = getDefaultElevation(cell.latitude, cell.longitude)))))
  }

  /**
   * @todo Fix fake scale...currenly 0 to 255 metres.
   * @todo average over an area instead of just taking a point.
   */
  protected def getDefaultElevation(latitude: Latitude, longitude: Longitude): Height = {
    val y = rescaleLatitudeToPixel(latitude.angle.toDegrees)
    val x = rescaleLongitudeToPixel(longitude.angle.toDegrees)
    val elevation = new java.awt.Color(defaultElevation.getRGB(x, y)).getRed
    Height(Meters(elevation))
  }

  protected lazy val defaultElevation: BufferedImage =
    javax.imageio.ImageIO.read(new java.io.File("data/elevation.bmp"))

  protected lazy val rescaleLatitudeToPixel =
    rescaleToInt(-MaxLat, MaxLat, defaultElevation.getHeight, 0)

  protected lazy val rescaleLongitudeToPixel =
    rescaleToInt(-MaxLon, MaxLon, 0, defaultElevation.getWidth)

  protected def rescaleToInt(minXIn: Double, maxXIn: Double, minXOut: Int, maxXOut: Int) = {
    val inRange = maxXIn - minXIn
    val outRange = maxXOut - minXOut
    val scale = outRange / inRange
    (x: Double) => ((x - minXIn) * scale + minXOut).toInt
  }

} // end of ElevationInput

// vim: set ts=2 sw=2 et:
