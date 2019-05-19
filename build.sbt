name := """CrFx-HmWork"""
organization := "it.tostao"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  guice,
  javaWs,
  "org.apache.commons" % "commons-lang3" % "3.9",
  "org.hamcrest" % "hamcrest-all" % "1.3" % "test"
)
