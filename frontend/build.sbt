name := """frontend"""
organization := "cmu"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies ++= Seq(
   ehcache,
  "com.google.code.gson" % "gson" % "2.8.5",
  "com.typesafe.play" %% "play-ws" % "2.6.19",
  "org.json" % "json" % "20180813"
)
