name := """scala-test"""
organization := "com.austinito"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.11"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "5.1.0",
  "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0"
)

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies ++= Seq(
  guice,
  "com.h2database" % "h2" % "1.4.200",
  "com.typesafe.play" %% "play-slick" % "5.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0"
)

libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "3.12.4" % Test,
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.austinito.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.austinito.binders._"
