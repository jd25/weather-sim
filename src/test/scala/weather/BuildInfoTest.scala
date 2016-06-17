package weather

import org.scalatest._

class BuildInfoTest extends FlatSpec with Matchers {

  "BuildInfo.toString" should "be non-empty" in {
    BuildInfo.toString should not be empty
  }

} // end of BuildInfoTest

// vim: set ts=2 sw=2 et:
