name := """asteroids"""
organization := "com.irysan"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"
val playVersion = "2.9.2"
val testVersion = "3.2.19"

libraryDependencies ++= Seq(
  guice,
  ws,
  "com.typesafe.play" %% "play-ahc-ws" % playVersion,
  "com.typesafe.play" %% "play-cache" % playVersion,
"com.github.ben-manes.caffeine" % "caffeine" % "3.1.8",
  "com.typesafe.play" %% "play-test" % playVersion % Test, // Add Play Test dependency
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test, // Add ScalaTest+Play dependency
  "org.scalatest" %% "scalatest" % testVersion % Test // Add ScalaTest dependency
)
dependencyOverrides ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.2.0"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.irysan.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.irysan.binders._"
