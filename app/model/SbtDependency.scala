package model

import scala.util.parsing.combinator._
import scala.language.existentials
import scala.language.implicitConversions

/**
 * @author Anton Tychyna
 */
case class SbtDependency(groupId: String, artifactId: String, revision: String, configuration: Option[String] = None, withScalaVersion: Boolean = false) {
  def dependencyDefinition = {
    val d = s"${'"'}$groupId${'"'} ${if (withScalaVersion) "%%" else "%"} ${'"'}$artifactId${'"'} % ${'"'}$revision${'"'}"
    if (configuration.isDefined) {
      d + " % \"" + configuration.get + "\""
    } else {
      d
    }
  }
}

object SbtDependency {
  implicit def fromMavenDependency(d: MavenDependency): SbtDependency = {
    val scope = d.scope match {
      case s if s != MavenScope.Provided && s != MavenScope.System => s.toString
      case _ => "compile"
    }
    SbtDependency(d.groupId, d.artifactId, d.version, Some(scope))
  }

  object SbtParser extends SbtDependencyParser

  class SbtTransformer {
    def parse(s: String): SbtParser.ParseResult[SbtDependency] = SbtParser.parse(SbtParser.dep, s)
  }

  def parse(s: String) = new SbtTransformer().parse(s)
}

trait SbtDependencyParser extends RegexParsers {
  def string = '\"' ~> """[^\n"]*""".r <~ '\"'

  def artifact: Parser[(String, Boolean)] = """%{1,2}\s*""".r ~ string ^^ {case a ~ b => (b, a.startsWith("%%"))}

  def version = """%\s*""".r ~> string

  def configuration = opt( """%\s*""".r ~> string)

  def dep: Parser[SbtDependency] = string ~ artifact ~ version ~ configuration ^^ {
    case g ~ a ~ v ~ c => SbtDependency(g, a._1, v, c, a._2)
  }
}
