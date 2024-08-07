package controllers

import fixtures.NeoDataFixture
import model.{CloseApproachData, EstimatedDiameter, MinMax, NearEarthObject, NeoData}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._
import services.NasaService

import scala.concurrent.Future

class NeoDataControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with NeoDataFixture {

  when(mockNasaService.fetchNeoData(any(), any())).thenReturn(Future.successful(Right(neoData)))

  "NeoDataController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new NeoDataController(stubControllerComponents(), mockNasaService)
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("NEO Data search")
    }

    "render the index page from the application" in {
      val controller = inject[NeoDataController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("NEO Data search")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("NEO Data search")
    }

    "render the list page when 'get' from the router" in {
      val request = FakeRequest(GET, "/neo?startDate=2023-09-15&endDate=2023-09-22")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Near Earth Objects Data")
    }

    "render the list page when 'post' from the router" in {
      val request = FakeRequest(POST, "/neo").withFormUrlEncodedBody(
        ("startDate", "2023-09-15"),
        ("endDate", "2023-09-22")
      )

      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Near Earth Objects Data")
    }
  }
}
