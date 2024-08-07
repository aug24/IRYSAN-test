package services

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorSystem
import akka.stream.Materializer
import cache.CaffeineCache
import fixtures.NeoDataFixture
import model.NeoData
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play._
import play.api.Application
import play.api.cache.SyncCacheApi
import play.api.inject._
import play.api.libs.json.Json
import play.api.test._
import play.api.libs.ws._
import play.api.libs.ws.ahc.cache.Cache
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class NasaServiceSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with NeoDataFixture {
  implicit lazy val wsRequest: WSRequest = mock[WSRequest]
  implicit lazy val wsResponse: WSResponse = mock[WSResponse]
  when(wsResponse.json).thenReturn(Json.toJson(neoData))
  implicit lazy val wsClient: WSClient = mock[WSClient]
  when(wsClient.url(any())).thenReturn(wsRequest)
  when(wsRequest.withQueryStringParameters(any())).thenReturn(wsRequest)
  when(wsRequest.get()).thenReturn(Future.successful(wsResponse))
  implicit lazy val ec: ExecutionContext = inject[ExecutionContext]

  "NasaService" should {

    "fetch the sample" in {
      val service = new NasaService(wsClient, new CaffeineCache)
      val dataEventually = service.fetchNeoData("2015-09-07", "2015-09-08")
      val data = await(dataEventually)
      // Really we should test for a bunch of things.
      assert(data.isRight)
      val result = data.getOrElse({
        throw new RuntimeException("Could not get Value")
      })
      assert(result.near_earth_objects.size > 0)
    }
  }
}