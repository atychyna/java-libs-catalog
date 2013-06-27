package model

/**
 * @author Anton Tychyna
 */
case class SbtDependency(groupId: String, artifactId: String, revision: String, configuration: Option[String] = None, withScalaVersion: Boolean = false) {
  def dependencyDefinition = {
    val d = s"$groupId ${if (withScalaVersion) "%%" else "%"} $artifactId % $revision"
    if (configuration.isDefined) {
      d + " % " + configuration.get
    } else {
      d
    }
  }
}

object SbtDependency {
  implicit def fromMavenDependency(d:MavenDependency) = {
    val scope = d.scope match {
      case s if s != MavenScope.Provided && s != MavenScope.System => s.toString
      case _ => "compile"
    }
    SbtDependency(d.groupId,d.artifactId,d.version,Some(scope))
  }
}