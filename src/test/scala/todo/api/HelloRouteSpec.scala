package todo.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpecLike}

class HelloRouteSpec extends WordSpecLike with Matchers with ScalatestRouteTest {

  import HelloRoute._

  "hello route" should {

    "hello!!" in {

      Get("/hello?name=ME") ~> helloRoute ~> check {
        responseAs[String] shouldBe "Hello ME!!"
      }
    }
  }
}
