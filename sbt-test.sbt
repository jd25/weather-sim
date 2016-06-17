libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.1" % "test"

libraryDependencies += "org.openjdk.jmh" % "jmh-core" % "1.12"

addCommandAlias("testCoverage", "; clean; coverage; test; coverageReport; coverageOff")

enablePlugins(JmhPlugin)

