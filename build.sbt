ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.10"

val CatsEffectVersion = "3.4.8"

lazy val root = (project in file("."))
  .settings(
    name := "essential-effects-ce",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    )
  )
