package services

import cache.CaffeineCache
import model.{ErrorResponse, NearEarthObject, NeoData}
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess}
import play.api.libs.ws._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import play.api.cache.SyncCacheApi
import services.NasaService.sort

import scala.concurrent.duration.DurationInt

@Singleton
class NasaService @Inject()(ws: WSClient, cache: CaffeineCache)(implicit ec: ExecutionContext) extends Logging {

  val baseUrl = "https://api.nasa.gov/neo/rest/v1/feed"
  val startDateLabel = "start_date"
  val endDateLabel = "end_date"
  val apiKeyLabel = "api_key"
  val apiKeyValue = "DEMO_KEY"

  def fetchNeoData(startDate: String, endDate: String): Future[Either[ErrorResponse, NeoData]] = {
    val key = startDate + endDate
    cache.get(key) match {
      case Some(data) =>
        logger.info("Cache hit")
        Future.successful(Right(data))
      case None =>
        logger.warn("Cache miss")
        fetchNeoDataWithoutCache(startDate, endDate)
    }
  }

  // This should expose some cache miss metrics
  private def fetchNeoDataWithoutCache(startDate: String, endDate: String): Future[Either[ErrorResponse, NeoData]] = {
    val key = startDate + endDate
    ws.url(baseUrl).withQueryStringParameters(
      (startDateLabel, startDate),
      (endDateLabel, endDate),
      (apiKeyLabel, apiKeyValue)
    ).get().map { response =>
      response.json.validate[NeoData] match {
        case JsSuccess(neoData, _) =>
          val sortedNeoData = sort(neoData)
          cache.set(key, neoData)
          Right(neoData)
        case JsError(errors) =>
        logger.warn(s"Failed to fetch with $startDate and $endDate")
          response.json.validate[ErrorResponse] match {
          case JsSuccess(errorResponse, _ ) => Left(errorResponse)
          case JsError(moreErrors) => Left(ErrorResponse(response.status, response.statusText, response.body, errors.toString() + moreErrors.toString()))
        }
      }
    }
  }

}

object NasaService {
  def sort(neoData:NeoData): NeoData = {
    val nearEarthObjects = neoData.near_earth_objects
    val sortedNearEarthObjects:Map[String, Seq[NearEarthObject]] = nearEarthObjects.map{case (k, v) =>
      (k -> v.sortBy(_.name))
    }
    neoData.copy(near_earth_objects = sortedNearEarthObjects)
  }
}