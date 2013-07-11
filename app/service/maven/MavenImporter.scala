package service.maven

import model.{MavenDependency, Project}
import java.io.{InputStream, FileInputStream, File}
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
    var p: Project = null
    using(s) {
      s =>
        val m = reader.read(s)
        p = Project(
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
    }
    p
  }
}
