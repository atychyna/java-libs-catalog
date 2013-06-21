package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import _root_.configuration.{injector => i, _}
import service.ProjectService

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {

  "Application" should {
    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone
      }
    }

    "render the index page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain("Recent projects")
      }
    }

    "list projects" in {
      running(FakeApplication()) {
        val projects = route(FakeRequest(GET, "/project")).get

        status(projects) must equalTo(OK)

        val projectService = i.bean[ProjectService].get
        val p = projectService.all
        p must not be empty

        for (pr <- p) contentAsString(projects) must contain(pr.name)
      }
    }

    "show specific project" in {
      running(FakeApplication()) {
        val projectService = i.bean[ProjectService].get
        val p = projectService.findByName("Apache Maven")
        p must beSome

        val project = route(FakeRequest(GET, "/project/" + p.get.id)).get
        status(project) must equalTo(OK)
        contentAsString(project) must contain(p.get.name) and contain(p.get.url)
      }
    }
  }
}