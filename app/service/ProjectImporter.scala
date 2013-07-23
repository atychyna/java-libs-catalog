package service

import java.io.InputStream
import model.Project

/**
 * @author Anton Tychyna
 */
trait ProjectImporter {
  def importFromMavenPOM(s: InputStream): Either[Exception, Project]
}
