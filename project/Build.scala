import sbt._
import Keys._
import play.Project._

object ApplicationSettings {
  val appName = "java-libs-catalog"
  val groupId = "org.aaa"
  val appVersion = "1.0-SNAPSHOT"

  val scalaVersion = "2.10.1"

  val appDependencies = Seq(
    "org.mongodb" %% "casbah" % "2.6.0",
    "com.novus" %% "salat-core" % "1.9.2-SNAPSHOT"
  )

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
    libraryDependencies ++= appDependencies,
    resolvers ++= s.resolvers
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
