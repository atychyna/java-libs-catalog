package model

import scala.xml.{Elem, PrettyPrinter}

/**
 * @author Anton Tychyna
 */
case class MavenDependency(groupId: String, artifactId: String, version: String, scope: MavenScope.Value = MavenScope.Compile) {
  require(groupId != null, "GroupId can't be null")
  require(artifactId != null, "ArtifactId can't be null")
  require(version != null, "Version can't be null")

  def dependencyDefinition(implicit p: PrettyPrinter) = {
    val d: Elem =
    <dependency>
      <groupId>{groupId}</groupId>
      <artifactId>{artifactId}</artifactId>
      <version>{version}</version>
    </dependency>
    import MavenScope._
    p.format(
      if (Set(Provided, System, Compile) contains scope) {
        d
      } else {
        d.copy(child = d.child :+ <scope>{scope}</scope>)
      }
    )
  }
}

object MavenScope extends Enumeration {
  val Compile = Value("compile")
  val Provided = Value("provided")
  val Runtime = Value("runtime")
  val Test = Value("test")
  val System = Value("system")
}
