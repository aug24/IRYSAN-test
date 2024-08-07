package fixtures

import model.{EstimatedDiameter, MinMax, NearEarthObject, NeoData}
import org.scalatestplus.mockito.MockitoSugar.mock
import services.NasaService

trait NeoDataFixture {

  private val nearEarthObject: NearEarthObject = NearEarthObject(
    "0", "Earth", EstimatedDiameter(MinMax(0, 0), MinMax(0, 0), MinMax(0, 0), MinMax(0, 0)), false, Seq.empty
  )
  val neoDataMap = Map("2015-09-07" -> Seq(nearEarthObject))
  val neoData: NeoData = NeoData(1, neoDataMap)
  val mockNasaService = mock[NasaService]

}
