name := """frontend"""
organization := "cmu"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies ++= Seq(
   ehcache,
  "com.typesafe.play" %% "play-ws" % "2.6.19"
)
