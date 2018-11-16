name := """backend"""
organization := "cmu"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies ++= Seq(
  "com.google.code.gson" % "gson" % "2.8.5",
  "org.neo4j" % "neo4j-jdbc-driver" % "3.4.0",
  "org.jsoup" % "jsoup" % "1.11.2", ws
)
