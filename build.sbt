name := "DMap"

version := "0.1"

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scalatest" %% "scalatest" % "3.0.0"
)