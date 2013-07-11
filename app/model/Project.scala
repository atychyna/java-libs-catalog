package model

import org.bson.types.ObjectId
import com.novus.salat.annotations._

/**
 * @author Anton Tychyna
 */
case class Project(id: ObjectId = new ObjectId,
                   name: String,
                   description: String,
                   url: String,
                   categories: Seq[ObjectId] = Seq(),
                   wiki: Option[String] = None,
                   bugTracker: Option[String] = None,
                   scm: Option[String] = None,
                   mavenDependency: Option[MavenDependency] = None,
                   sbtDependency: Option[SbtDependency] = None,
                   relatedProjects: Seq[ObjectId] = Seq()) {
  require(name != null, "Name can't be null")
  require(description != null, "Description can't be null")
  require(url != null, "Url can't be null")
  @Persist val nameLowerCase = name.toLowerCase
}