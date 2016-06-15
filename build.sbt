// project info

name := "weather-sim"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

enablePlugins(BuildInfoPlugin)

buildInfoPackage := "weather"
