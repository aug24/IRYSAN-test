package model

import play.api.libs.json._

case class DateRange(startDate: String, endDate: String)

object DateRange {
  implicit val formatter = Json.format[DateRange]
}

case class EstimatedDiameter(kilometers: MinMax, meters: MinMax, miles: MinMax, feet: MinMax)
case class MinMax(estimated_diameter_min: Double, estimated_diameter_max: Double)
case class CloseApproachData(close_approach_date: String, relative_velocity: RelativeVelocity, miss_distance: MissDistance)
case class RelativeVelocity(kilometers_per_hour: String)
case class MissDistance(kilometers: String)
case class NearEarthObject(id: String, name: String, estimated_diameter: EstimatedDiameter, is_potentially_hazardous_asteroid: Boolean, close_approach_data: Seq[CloseApproachData])
case class NeoData(element_count: Int, near_earth_objects: Map[String, Seq[NearEarthObject]])

object NeoData {
  implicit val minMaxFormat: OFormat[MinMax] = Json.format[MinMax]
  implicit val estimatedDiameterFormat: OFormat[EstimatedDiameter] = Json.format[EstimatedDiameter]
  implicit val relativeVelocityFormat: OFormat[RelativeVelocity] = Json.format[RelativeVelocity]
  implicit val missDistanceFormat: OFormat[MissDistance] = Json.format[MissDistance]
  implicit val closeApproachDataFormat: OFormat[CloseApproachData] = Json.format[CloseApproachData]
  implicit val nearEarthObjectFormat: OFormat[NearEarthObject] = Json.format[NearEarthObject]
  implicit val neoDataFormat: OFormat[NeoData] = Json.format[NeoData]
}

case class ErrorResponse(
  code: Int,
  http_error: String,
  error_message: String,
  request: String
)

object ErrorResponse {
  implicit val formatter = Json.format[ErrorResponse]
}