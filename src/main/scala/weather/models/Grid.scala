package weather.models

import java.time.ZonedDateTime

import squants.space.Angle
import squants.space.Degrees

import weather.units.Angles._

final case class Grid(cells: Seq[Seq[Cell]]) {
  def topLeftCell: Option[Cell] = cells.headOption.flatMap(_.headOption)
  def bottomRightCell: Option[Cell] = cells.lastOption.flatMap(_.lastOption)

  def top: Option[Latitude] = topLeftCell.map(_.latitude)
  def left: Option[Longitude] = topLeftCell.map(_.longitude)
  def bottom: Option[Latitude] = bottomRightCell.map(_.latitude)
  def right: Option[Longitude] = bottomRightCell.map(_.longitude)

  def timestamp: Option[ZonedDateTime] = topLeftCell.map(_.timestamp)

  /** Obtain the Cell for a given latitude and longitude, if in range. */
  def lift(latitude: Latitude, longitude: Longitude): Option[Cell] = {
    for {
      left <- left if left.angle <= longitude.angle
      right <- right if longitude.angle <= right.angle
      top <- top if latitude.angle <= top.angle
      bottom <- bottom if bottom.angle <= latitude.angle
      rows = cells.size - 1
      cols = cells.headOption.map(_.size).getOrElse(0) - 1
      latIndex = Math.round(rows * (latitude.angle - top.angle) / (bottom.angle - top.angle))
      lonIndex = Math.round(cols * (longitude.angle - left.angle) / (right.angle - left.angle))
      row <- cells.lift(latIndex.toInt)
      cell <- row.lift(lonIndex.toInt)
    } yield cell
  }

}

final case class GridHistory(cells: Seq[Seq[Seq[Cell]]])

object GridHistory {

  val empty = GridHistory(Nil)

}

object Grid {

  val empty = Grid(Nil)

  /**
   * Returns a Grid of square Cells, which at least covers
   * the geograhical area bounded by the given lat/lon.
   * @param top a greater Latitude than the bottom
   * @param bottom a lesser Latitude than the top
   * @param left a lesser Longitude than the right
   * @param right a greater Longitude than the left
   * @param cellsOnLongSide a grid must have 2 or more cells horizontally
   * and vertically (minimum grid is 2x2=4).
   */
  def uniform(
    top: Latitude, left: Longitude, bottom: Latitude, right: Longitude, cellsOnLongSide: Int,
    timestamp: ZonedDateTime, customize: Cell => Cell = identity
  ): Option[Grid] =
    (left.angle < right.angle && bottom.angle < top.angle && cellsOnLongSide > 1) match {
      case true =>
        val latSpan: Double = (bottom.angle - top.angle).toDegrees
        val lonSpan: Double = (right.angle - left.angle).toDegrees
        val (tallSkinny, squareCellSideAngle) = maxMinAvg(cellsOnLongSide, latSpan, lonSpan, 2)
        val (latCellCount, lonCellCount) = if (latSpan > lonSpan) tallSkinny else tallSkinny.swap
        val cells = makeCells(
          top, left, Degrees(squareCellSideAngle), latCellCount, lonCellCount,
          timestamp, customize
        )
        Some(Grid(cells))
      case _ => None
    }

  /** Size our grid to the desired resolution of square cells. */
  protected def maxMinAvg(maxCellsPerSide: Int, a: Double, b: Double, floor: Int) = {
    val longSide: Double = Math.min(Math.abs(a), Math.abs(b))
    val squareCellSideAngle = Math.max(a, b) / (maxCellsPerSide - 1)
    val minCellsPerSide: Int = Math.max(floor, Math.ceil(longSide / squareCellSideAngle).toInt + 1)
    ((maxCellsPerSide, minCellsPerSide), squareCellSideAngle)
  }

  /**
   * Iterate over latitude and longidute (rows and columns) to make a
   * grid. Verbose but simple.
   */
  protected def makeCells(
    top: Latitude, left: Longitude, size: Angle, numLatCells: Int, numLonCells: Int,
    timestamp: ZonedDateTime, customize: Cell => Cell
  ) = {
    def makeRow(lat: Latitude) = Vector.tabulate(numLonCells) { col =>
      val lon = Longitude(left.angle + size * col.toDouble)
      val cell = Cell(latitude = lat, longitude = lon, timestamp = timestamp)
      customize(cell)
    }
    for {
      latOffset <- Vector.tabulate(numLatCells)(row => size * row.toDouble)
      lat = Latitude(top.angle - latOffset)
    } yield makeRow(lat)
  }

} // end of Grid

// vim: set ts=2 sw=2 et:
