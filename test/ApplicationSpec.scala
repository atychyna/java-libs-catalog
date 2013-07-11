import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import _root_.configuration.{injector => i, _}
import service.ProjectService
import specs.WithGuiceApplication

class ApplicationSpec extends Specification {

  "Application" should {
    "send 404 on a bad request" in new WithGuiceApplication(classOf[TestModule]) {
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithGuiceApplication(classOf[TestModule]) {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain("Recent projects")
    }

    "list projects" in new WithGuiceApplication(classOf[TestModule]) {
      val projects = route(FakeRequest(GET, "/projects/all")).get

      status(projects) must equalTo(OK)

      val projectService = i.bean[ProjectService].get
      val p = projectService.all
      p must not be empty

      for (pr <- p) contentAsString(projects) must contain(pr.name)
    }

    "show specific project" in new WithGuiceApplication(classOf[TestModule]) {
      val projectService = i.bean[ProjectService].get
      val p = projectService.findByName("Apache Maven")
      p must beSome

      val project = route(FakeRequest(GET, "/projects/" + p.get.id)).get
      status(project) must equalTo(OK)
      contentAsString(project) must contain(p.get.name) and contain(p.get.url)
    }
  }
}