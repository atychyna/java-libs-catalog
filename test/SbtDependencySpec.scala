import model.SbtDependency
import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification
import SbtDependency.SbtParser.ParseResult

/**
 * @author Anton Tychyna
 */
class SbtDependencySpec extends Specification {
  val beSuccess: Matcher[ParseResult[_]] = ((_: ParseResult[_]).successful, "parsing was unsucessfull")

  "SbtDependency" should {
    """parse "a" % "aa" % "1.0"""" in {
      val result = SbtDependency.parse( """"a" % "aa" % "1.0"""")
      result must beSuccess

      val d = result.get
      d.groupId must beEqualTo("a")
      d.artifactId must beEqualTo("aa")
      d.revision must beEqualTo("1.0")
      d.withScalaVersion must beFalse
    }
    """parse "a" %% "aa" % "1.0"""" in {
      val result = SbtDependency.parse( """"a" %% "aa" % "1.0"""")
      result must beSuccess

      val d = result.get
      d.groupId must beEqualTo("a")
      d.artifactId must beEqualTo("aa")
      d.revision must beEqualTo("1.0")
      d.withScalaVersion must beTrue
    }
    """parse "a" % "aa" % "1.0" % "test"""" in {
      val result = SbtDependency.parse( """"a" % "aa" % "1.0" % "test"""")
      result must beSuccess

      val d = result.get
      d.groupId must beEqualTo("a")
      d.artifactId must beEqualTo("aa")
      d.revision must beEqualTo("1.0")
      d.withScalaVersion should beFalse
      d.configuration must beSome("test")
    }
    """parse "a" %% "aa" % "1.0" % "test"""" in {
      val result = SbtDependency.parse( """"a" %% "aa" % "1.0" % "test"""")
      result must beSuccess

      val d = result.get
      d.groupId must beEqualTo("a")
      d.artifactId must beEqualTo("aa")
      d.revision must beEqualTo("1.0")
      d.withScalaVersion should beTrue
      d.configuration must beSome("test")
    }
    """fail on "a"" %% "aa" % "1.0" % "test"""" in {
      val result = SbtDependency.parse( """"a"" %% "aa" % "1.0" % "test"""")
      result must not(beSuccess)
    }
  }
}
