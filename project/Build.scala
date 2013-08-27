import sbt._
import Keys._
import play.Project._

object ApplicationSettings {
  val appName = "java-libs-catalog"
  val groupId = "org.aaa"
  val appVersion = "1.0-SNAPSHOT"

  val scalaVersion = "2.10.2"

  val appDependencies = Seq(
    "org.mongodb" %% "casbah" % "2.6.0",
    "com.novus" %% "salat-core" % "1.9.2-SNAPSHOT",
    "com.google.inject" % "guice" % "3.0",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    "se.radley" %% "play-plugins-salat" % "1.2",
    "org.apache.maven" % "maven-model" % "3.0.5",
    "eu.henkelmann" % "actuarius_2.10.0" % "0.2.6",
    "me.lessis" %% "hubcat" % "0.1.1"
  )

  val testDependencies = Seq(
    "org.seleniumhq.selenium" % "selenium-java" % "2.33.0"
  ) map (_ % "test")

  val resolvers = Seq(
    "repo.novus rels" at "http://repo.novus.com/releases/",
    "repo.novus snaps" at "http://repo.novus.com/snapshots/",
    "typesafe" at "http://repo.typesafe.com/typesafe/repo"
  )
}

object ApplicationBuild extends Build {

  import ApplicationSettings.{scalaVersion => _, resolvers => _, _}

  val s = ApplicationSettings

  override val settings = super.settings ++ Seq(
    name := appName,
    organization := groupId,
    version := appVersion,
    scalaVersion := s.scalaVersion,
    libraryDependencies ++= appDependencies ++ testDependencies,
    resolvers ++= s.resolvers,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    routesImport += "se.radley.plugin.salat.Binders._",
    templatesImport += "org.bson.types.ObjectId"
  )

}
