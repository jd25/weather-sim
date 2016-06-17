scapegoatVersion := "1.2.0"

scapegoatDisabledInspections := Seq("RedundantFinalModifierOnCaseClass")

transitiveClassifiers := Seq("sources")

val Linting = config("lint")

inConfig(Linting) {
  Defaults.compileSettings ++ Seq(
    wartremoverErrors ++= Warts.allBut(Wart.DefaultArguments, Wart.Throw /* pattern matching */),
    scalacOptions += "-Ywarn-unused-import",
    scalacOptions += "-Ywarn-dead-code", // slow
//  scalacOptions += "-Xfatal-warnings",
    scalacOptions ++= Seq("-Xlint", "-Yno-adapted-args", "-Ywarn-numeric-widen", "-Ywarn-value-discard")
  )
}

sources in Linting <<= (sources in Compile)

lazy val lint = taskKey[Unit]("Linting and static analysis")

lint := {
  println((compile in Linting).value)
  (scapegoat in Compile).value
  (scalastyle in Compile).toTask("").value
}

// Demo is end of the world...

wartremoverExcluded += baseDirectory.value / "src" / "main" / "scala" / "weather" / "Demo.scala"

scapegoatIgnoredFiles := Seq(".*/Demo.scala")

coverageExcludedPackages := "weather.Demo"
