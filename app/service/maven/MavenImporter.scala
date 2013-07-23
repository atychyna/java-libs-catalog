package service.maven

import model.{MavenDependency, Project}
import java.io.InputStream
import service.ProjectImporter
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import util._

/**
 * @author Anton Tychyna
 */
class MavenImporter extends ProjectImporter {
  def importFromMavenPOM(s: InputStream) = {
    require(s != null, "Pom input stream must not be null")
    val reader = new MavenXpp3Reader
    var r: Either[Exception, Project] = null
    using(s) {
      s =>
        r = try {
          val m = reader.read(s)
          var p = Project(
            name = m.getName,
            description = m.getName,
            url = m.getUrl,
            mavenDependency = Some(MavenDependency(groupId = m.getGroupId, artifactId = m.getArtifactId, version = m.getVersion))
          )
          if (m.getIssueManagement != null) {
            p = p.copy(bugTracker = Some(m.getIssueManagement.getUrl))
          }
          if (m.getScm != null) {
            p = p.copy(scm = Some(m.getScm.getUrl))
          }
          Right(p)
        } catch {
          case e: Exception => Left(e)
        }
    }
    r
  }
}
