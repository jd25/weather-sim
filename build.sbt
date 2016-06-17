// project info

name := "weather-sim"

version := "0.1.1-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

enablePlugins(BuildInfoPlugin)

buildInfoPackage := "weather"

scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", "README.md")

