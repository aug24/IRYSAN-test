package controllers

import controllers.NeoDataController.{badRequest, getErrorNeoIdNotFound, getErrorNotFound, getStringFromFormOrQuery}
import model.ErrorResponse
import play.api.Logging
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND}
import play.api.mvc.Results.BadRequest
import play.api.mvc._
import services.NasaService

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class NeoDataController @Inject()(val controllerComponents: ControllerComponents, val nasaService: NasaService) extends BaseController with Logging {

  val startDateLabel = "startDate"
  val endDateLabel = "endDate"
  val neoIdLabel = "neoId"

  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def neo(): Action[AnyContent] = Action.async({ implicit request: Request[AnyContent] =>
    // All of this can be done with a Play Form and form binding
    // This approach is partly showing I understand the underlying principles of html/http
    // and also because I wanted to trivially support both GET and POST.
    val startDateMaybe = getStringFromFormOrQuery(startDateLabel)
    val endDateMaybe = getStringFromFormOrQuery(endDateLabel)
    (startDateMaybe, endDateMaybe) match {
      case (None, _) => badRequest(getErrorNotFound(request, "start date"))
      case (_, None) => badRequest(getErrorNotFound(request, "end date"))
      case (Some(startDate), Some(endDate)) => nasaService.fetchNeoData(startDate, endDate).map {
        case Right(data) => Ok(views.html.neodata(data, startDate, endDate))
        case Left(error) => BadRequest(views.html.badRequest(error))
      }.map(_.withSession(request.session)) // Not strictly needed but we might want to cache the results via the session id rather than refetch them.
    }
  })

  def detail(): Action[AnyContent] = Action.async({ implicit request: Request[AnyContent] =>
    val startDateMaybe = request.getQueryString(startDateLabel)
    val endDateMaybe = request.getQueryString(endDateLabel)
    val neoIdMaybe = request.getQueryString(neoIdLabel)
    ((startDateMaybe, endDateMaybe, neoIdMaybe) match {
      case (None, _, _) => badRequest(getErrorNotFound(request, "start date"))
      case (_, None, _) => badRequest(getErrorNotFound(request, "end date"))
      case (_, _, None) => badRequest(getErrorNotFound(request, "neo id"))
      case (Some(startDate), Some(endDate), Some(neoId)) =>
        nasaService.fetchNeoData(startDate, endDate).map {
          case Right(data) => data.near_earth_objects.values.flatMap(neos => neos.toList.map(n => n.id -> n)).toMap.get(neoId) match {
            case None => NotFound(views.html.badRequest(getErrorNeoIdNotFound(request, neoId)))
            case Some(datum) => Ok(views.html.neodataDetail(datum))
          }
          case Left(error) => BadRequest(views.html.badRequest(error))
        }
    }).map(_.withSession(request.session)) // Not strictly needed but we might want to cache the results via the session id rather than refetch them.
  })

}

// All non-stateful methods move to the companion object.  This makes them more easily testable.
object NeoDataController {
  private def getErrorNotFound(request: Request[AnyContent], missingField: String) =
    ErrorResponse(BAD_REQUEST, "Bad Request", s"No $missingField found", request.uri)

  private def getErrorNeoIdNotFound(request: Request[AnyContent], neoId: String) =
    ErrorResponse(NOT_FOUND, "Not Found", s"No neo found for date range with id $neoId", request.uri)

  private def badRequest(response: ErrorResponse) = Future.successful(BadRequest(views.html.badRequest(response)))

  private def getStringFromFormOrQuery(label: String)(implicit request: Request[AnyContent]): Option[String] =
    request.body.asFormUrlEncoded match {
      case None => request.getQueryString(label)
      case Some(form) => form.get(label).flatMap(_.headOption)
    }
}