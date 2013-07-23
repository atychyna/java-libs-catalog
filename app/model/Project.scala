package model

import org.bson.types.ObjectId
import util.stringForUrl
import com.novus.salat.annotations._
import org.joda.time.DateTime

/**
 * @author Anton Tychyna
 */
case class Project(id: ObjectId = new ObjectId,
                   name: String,
                   description: String,
                   url: String,
                   languages: Set[ProjectLang.Value] = ProjectLang.ValueSet(ProjectLang.Java),
                   categories: Seq[ObjectId] = Seq(),
                   wiki: Option[String] = None,
                   bugTracker: Option[String] = None,
                   scm: Option[String] = None,
                   mavenDependency: Option[MavenDependency] = None,
                   sbtDependency: Option[SbtDependency] = None,
                   relatedProjects: Seq[ObjectId] = Seq(),
                   created: DateTime = DateTime.now,
                   lastModified: DateTime = DateTime.now) {
  require(name != null, "Name can't be null")
  require(description != null, "Description can't be null")
  require(url != null, "Url can't be null")
  @Persist val nameLowerCase = name.toLowerCase
  val urlFriendlyName = stringForUrl(name)
  val hasDependencies = mavenDependency.isDefined || sbtDependency.isDefined
}

object ProjectLang extends Enumeration {
  val Scala = Value("Scala")
  val Java = Value("Java")
}