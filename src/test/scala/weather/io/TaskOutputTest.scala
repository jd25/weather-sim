package weather.io

import org.scalatest._

class TaskOutputTest extends FlatSpec with Matchers {

  "TaskOutputTest.testOutputFormat1Line" should "be true for spec examples" in {
    testOutputFormat1Lines(Iterator(
      "SYD|-33.86,151.21,39|2015-12-23T05:02:12Z|Rain|+12.5|1004.3|97",
      "MEL|-37.83,144.98,7|2015-12-24T15:30:55Z|Snow|-5.3|998.4|55",
      "ADL|-34.92,138.62,48|2016-01-03T12:35:37Z|Sunny|+39.4|1114.1|12"
    )) shouldBe true
  }

  it should "be false for too few columns" in {
    testOutputFormat1Lines(Iterator(
      "-33.86,151.21,39|2015-12-23T05:02:12Z|Rain|+12.5|1004.3|97"
    )) shouldBe false
  }

  it should "be false for wrong formats" in {
    testOutputFormat1Lines(Iterator(
      "MEL|-37.83,144.98,7|2015-12-24T15:30:55Z|Snow|-5.3|998.4|XX"
    )) shouldBe false
  }

  def testOutputFormat1Lines(lines: Iterator[String]) = {
    lines.forall(testOutputFormat1Line)
  }

  /** @todo Be more rigorous in validation */
  def testOutputFormat1Line(line: String) = {
    // Quick-and-dirty regex for:
    // SYD|-33.86,151.21,39|2015-12-23T05:02:12Z|Rain|+12.5|1004.3|97
    val fields = Seq(
      "[A-Z]{3,4}",
      "[-0-9.,]{12,18}",
      "[-0-9T:Z]{20}",
      "[A-Za-z]{3,10}",
      "[-+0-9.]{4,5}",
      "[0-9.]{5,6}",
      "[0-9]{1,3}"
    )
    line.matches(fields.mkString("[|]"))
  }

  "TaskOutput.format" should "be compliant" in {
    val station = new weather.stations.WeatherStationTest().PER.get
    val cell = weather.models.Cell(
      latitude = station.iataCode.latitude,
      longitude = station.iataCode.longitude
    )
    testOutputFormat1Lines(Iterator(
      TaskOutput.format(station, cell)
    )) shouldBe true
  }

} // end of TaskOutputTest

// vim: set ts=2 sw=2 et:
