import com.typesafe.sbt.web.SbtWeb
import play.PlayScala

name := "play-guice"

version := "1.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala,SbtWeb)

scalaVersion := "2.11.1"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
// Test dependencies
  "com.google.inject" % "guice" % "3.0",
    "javax.inject" % "javax.inject" % "1",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "org.webjars" %% "webjars-play" % "2.3.0-2",
    "org.webjars" % "angularjs" % "1.3.0",
  "org.webjars" % "angular-ui-bootstrap" % "0.11.2",
"org.webjars" % "bootstrap" % "3.1.1",
"org.webjars" % "jquery" % "2.1.0-2",
"org.webjars" % "requirejs" % "2.1.11-1"
)

Concat.parentDir := "public/main"

pipelineStages := Seq(concat)

Concat.groups := Seq(
  "all.js" -> group(((target in Assets).value / "web" / "coffeescript" / "main" / "javascripts" ) ** "*.js"),
  "allSelected.coffee" -> group(Seq( "javascripts/app.coffee")),
  "allPFinder.coffee" -> group(((sourceDirectory in Assets).value / "javascripts" ) ** "*.coffee")
)
