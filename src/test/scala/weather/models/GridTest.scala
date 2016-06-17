package weather.models

import org.scalatest._

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalacheck.Prop.classify

import java.time.ZonedDateTime

import squants.space.Degrees

import weather.stations.IATACode

import weather.units.Angles._
import weather.units.AnglesCheck._

/**
 * @todo test simulatons that go to the latitude/longitude extremities (eg poles).
 */
class GridTest extends FlatSpec with Matchers {

  val now = ZonedDateTime.now

  val latBottom = Latitude(Degrees(0))
  val lonLeft = Longitude(Degrees(0))
  val latTop = Latitude(Degrees(10))
  val lonRight = Longitude(Degrees(20))

  "Grid.uniform" should "be None for invalid inputs" in {
    Grid.uniform(latBottom, lonLeft, latBottom, lonLeft, -1, now) should be(None)
    Grid.uniform(latBottom, lonLeft, latBottom, lonLeft, 0, now) should be(None)
    Grid.uniform(latBottom, lonLeft, latBottom, lonLeft, 1, now) should be(None)
    Grid.uniform(latBottom, lonLeft, latBottom, lonLeft, 2, now) should be(None)
    Grid.uniform(latBottom, lonLeft, latBottom, lonLeft, 3, now) should be(None)

    Grid.uniform(latBottom, lonLeft, latTop, lonRight, -1, now) should be(None)
    Grid.uniform(latBottom, lonLeft, latTop, lonRight, 0, now) should be(None)
    Grid.uniform(latBottom, lonLeft, latTop, lonRight, 1, now) should be(None)
    Grid.uniform(latBottom, lonLeft, latTop, lonRight, 2, now) should be(None)
    Grid.uniform(latBottom, lonLeft, latTop, lonRight, 3, now) should be(None)

    Grid.uniform(latTop, lonLeft, latBottom, lonRight, -1, now) should be(None)
    Grid.uniform(latTop, lonLeft, latBottom, lonRight, 0, now) should be(None)
    Grid.uniform(latTop, lonLeft, latBottom, lonRight, 1, now) should be(None)
    Grid.uniform(latTop, lonLeft, latBottom, lonRight, 2, now) should not be (None)
    Grid.uniform(latTop, lonLeft, latBottom, lonRight, 3, now) should not be (None)
  }

  "Grid.uniform" should "have at least 2x2 cell squares" in {
    Grid.uniform(latTop, lonLeft, latBottom, lonRight, 2, now) match {
      case Some(grid) =>
        grid.cells.size should be >= 2
        grid.cells.head.size should be >= 2
        grid.left.get.angle should be <= lonLeft.angle // grid.left.get notGreater lonLeft should be(true)
        grid.right.get.angle should be >= lonRight.angle // grid.right.get.angle notLesser lonRight should be(true)
        grid.top.get.angle should be >= latTop.angle // grid.top.get.angle notBelow latTop should be(true)
        grid.bottom.get.angle should be <= latBottom.angle // grid.bottom.get below notAbove latBottom should be(true)
      case _ =>
        fail()
    }
  }

  "Grid.uniform" should "be span valid inputs correctly" in {
    // PER and SYD are distant in longitude but almost the same in latitude
    val PER = IATACode.ofLocation("PER").get
    val SYD = IATACode.ofLocation("SYD").get
    Grid.uniform(PER.latitude, PER.longitude, SYD.latitude, SYD.longitude, 1000, now) match {
      case Some(grid) =>
        grid.cells.head.size should be >= 1000
        grid.cells.size should be >= 50
        grid.left.get.angle should be <= PER.longitude.angle
        grid.right.get.angle should be >= SYD.longitude.angle
        grid.top.get.angle should be >= PER.latitude.angle
        grid.bottom.get.angle should be <= SYD.latitude.angle
      case _ =>
        fail()
    }
  }

} // end of GridTest

object GridCheck extends Properties("Grid") {

  import org.scalacheck.Prop.BooleanOperators

  property("Grid.left,left,bottom,right") = forAll {
    (lat1: Latitude, lon1: Longitude, lat2: Latitude, lon2: Longitude) =>
      (lat1.angle > lat2.angle && lon1.angle < lon2.angle) ==> {
        val now = java.time.ZonedDateTime.now
        val c1 = Cell(latitude = lat1, longitude = lon1, timestamp = now)
        val c2 = Cell(latitude = lat1, longitude = lon2, timestamp = now)
        val c3 = Cell(latitude = lat2, longitude = lon1, timestamp = now)
        val c4 = Cell(latitude = lat2, longitude = lon2, timestamp = now)
        val grid = Grid(Seq(Seq(c1, c2), Seq(c3, c4)))
        grid.timestamp == Some(now) &&
          grid.left == Some(lon1) &&
          grid.right == Some(lon2) &&
          grid.top == Some(lat1) &&
          grid.bottom == Some(lat2) &&
          grid.lift(lat1, lon1) == Some(c1) &&
          grid.lift(lat1, lon2) == Some(c2) &&
          grid.lift(lat2, lon1) == Some(c3) &&
          grid.lift(lat2, lon2) == Some(c4)
      }
  }

} // end of GridCheck

// vim: set ts=2 sw=2 et:
