package test

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import scala.util.parsing
import org.h2.util.json.JSONValue
import play.api.libs.json.JsValue
import play.filters.csrf.CSRFConfigProvider
import play.filters.csrf.CSRF._
import play.api.test.CSRFTokenHelper._
import scala.concurrent.ExecutionContext

class ApplicationSpec extends PlaySpec with GuiceOneAppPerTest {

  "Application" should {

    "get a random login prompt correctly" in {
      val request = FakeRequest(GET, "/api/loginPrompts/random")
      val randomResult = route(app, request).get

      // assertions
      status(randomResult) mustBe OK
      contentType(randomResult) mustBe Some("application/json")
      val json = contentAsJson(randomResult)
      (json \ "id").toOption mustBe defined
      (json \ "caption").toOption mustBe defined
      (json \ "imageUrl").toOption mustBe defined
    }

  }
}
