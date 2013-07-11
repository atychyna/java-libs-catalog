package service

import java.io.{InputStream, File}
import model.Project

/**
 * @author Anton Tychyna
 */
trait ProjectImporter {
  def importFromMavenPOM(s: InputStream): Project
}
