package todo.api.repository

import scala.concurrent.ExecutionContext

import slick.jdbc.H2Profile.api._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}

class TodoRepositorySpec extends WordSpecLike with Matchers with ScalaFutures {

  import Model._

  implicit val executor: ExecutionContext = ExecutionContext.global

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(200, Millis))

  val db = Database.forConfig("todo-slick-db")
  val todoRepository = TodoRepositoryImpl(db)

  "todo repository" should {

    "crud" in {
      val createdId = todoRepository.create(Todo(0, "test")).futureValue
      createdId.isRight shouldBe true
      createdId.getOrElse(0) shouldBe 1

      val findAll = todoRepository.findAll().futureValue
      findAll.isRight shouldBe true
      findAll.getOrElse(Seq.empty[Todo]).size shouldBe 1

      val findById = todoRepository.findById(createdId.getOrElse(0)).futureValue
      findById.isRight shouldBe true
      findById.getOrElse(None).nonEmpty shouldBe true

      val updated = todoRepository.update(Todo(createdId.getOrElse(0), "test2")).futureValue
      updated.isRight shouldBe true
      updated.getOrElse(0) shouldBe 1

      val deleted = todoRepository.delete(createdId.getOrElse(0)).futureValue
      deleted.isRight shouldBe true
      deleted.getOrElse(0) shouldBe 1
    }
  }
}
