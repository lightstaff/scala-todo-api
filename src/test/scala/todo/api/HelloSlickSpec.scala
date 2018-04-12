package todo.api

import scala.concurrent.ExecutionContext

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}
import slick.jdbc.H2Profile.api._

class HelloSlickSpec extends WordSpecLike with Matchers with ScalaFutures {

  import HelloSlick._

  implicit val executor: ExecutionContext = ExecutionContext.global

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(200, Millis))

  val db = Database.forConfig("hello-slick-db")
  val helloSlick = HelloSlick(db)

  "hello slick" should {

    "crud!!" in {
      // c: create
      val created = helloSlick.create(Hello(0, "ME")).futureValue
      created.id shouldBe 1

      // r: read
      val findAll = helloSlick.findAll().futureValue
      findAll.size shouldBe 1
      val findById = helloSlick.findById(created.id).futureValue
      findById.nonEmpty shouldBe true
      findById.foreach(a => a.id shouldBe created.id)

      // u: update
      val updated = helloSlick.update(Hello(created.id, "ME2")).futureValue
      updated shouldBe 1

      // d: delete
      val deleted = helloSlick.delete(created.id).futureValue
      deleted shouldBe 1
    }

  }

}
