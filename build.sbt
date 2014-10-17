name := "play-guice"

version := "1.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
// Test dependencies
  "com.google.inject" % "guice" % "3.0",
    "javax.inject" % "javax.inject" % "1",
    "org.mockito" % "mockito-core" % "1.9.5" % "test"
)
