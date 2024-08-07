package model

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Json

import scala.io.Source

class NeoDataTest extends AnyFunSuite {

  test("the model parses") {
    val filePath = "test/samples/asteroid.json"
    val fileContent = readFile(filePath)
    println(fileContent)
    assert(fileContent.nonEmpty, "File content should not be empty")
    val parsed = Json.parse(fileContent).validate[NeoData].get
    assert(parsed.element_count == 27)
    assert(parsed.near_earth_objects.size == 2)
    assert(parsed.near_earth_objects.keys.toList.contains("2015-09-08"))
    assert(parsed.near_earth_objects.keys.toList.contains("2015-09-07"))
    // I could add more but you get the idea.
  }

  def readFile(filePath: String): String = {
    val source = Source.fromFile(filePath)
    try {
      source.mkString
    } finally {
      source.close()
    }
  }
}