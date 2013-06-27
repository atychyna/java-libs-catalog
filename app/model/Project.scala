package model

import org.bson.types.ObjectId
import com.novus.salat.annotations._

/**
 * @author Anton Tychyna
 */
case class Project(id: ObjectId = new ObjectId,
                   name: String,
                   url: String,
                   categoryId: ObjectId,
                   wiki: Option[String] = None,
                   bugTracker: Option[String] = None,
                   scm: Option[String] = None,
                   mavenDependency: Option[MavenDependency] = None,
                   sbtDependency: Option[SbtDependency] = None,
                   relatedProjects: Seq[ObjectId] = Seq()) {
  @Persist val nameLowerCase = name.toLowerCase
}