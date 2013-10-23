import controllers.AddProject
import org.bson.types.ObjectId
import org.specs2.mutable._
import play.api.http.MimeTypes
import play.api.test.FakeRequest
import play.api.test.Helpers._
import configuration.{injector => i, _}
import service.{ProjectService, ProjectImporter}
import specs.WithGuiceApplication

/**
 * @author Anton Tychyna
 */
class AddProjectSpec extends Specification {
  "AddProject" should {
    "import from Maven pom.xml" in new WithGuiceApplication(classOf[TestModule]) {
      val pom = getClass.getResourceAsStream("pom.xml")
      pom must not beNull

      val addProject = i.bean[AddProject].get
      val projectImporter = i.bean[ProjectImporter].get
      val projectService = i.bean[ProjectService].get
      val either = projectImporter.importFromMavenPOM(pom)
      either must beRight
      val project = either.right.get.copy(categories = Seq(new ObjectId))
      project must not beNull

      val form = addProject.addProjectForm.fill(project)

      val review = route(FakeRequest(POST, "/reviewproject")
        .withHeaders(CONTENT_TYPE -> MimeTypes.FORM)
        .withFormUrlEncodedBody(form.data.toSeq: _*)).get
      status(review) must equalTo(OK)

      val result = route(FakeRequest(POST, "/createproject")
        .withHeaders(CONTENT_TYPE -> MimeTypes.FORM, COOKIE -> header(SET_COOKIE, review).get)
        .withFormUrlEncodedBody(form.data.toSeq: _*)).get
      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/projects/Test+project?isNew=true")

      val projectSaved = projectService.findById(project.id)
      projectSaved must beSome
      projectSaved.get.name must beEqualTo(project.name)
      projectSaved.get.url must beEqualTo(project.url)
      projectSaved.get.description must contain(project.description)
      projectSaved.get.categories must beEqualTo(project.categories)
    }

    "create project" in {
      todo
    }
  }
}


