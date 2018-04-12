package todo.api

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import org.scalatest.{Matchers, WordSpecLike}

import todo.api.actor.TodoActorJsonSerializer

class TodoRoutesSpec
    extends WordSpecLike
    with Matchers
    with ScalatestRouteTest
    with SprayJsonSupport
    with TodoActorJsonSerializer {

  import todo.api.actor.TodoActor._

  implicit val timeout: Timeout = Timeout(5.seconds)

  val actorMock: ActorRef = system.actorOf(Props(new Actor with ActorLogging {

    override def receive: Receive = {
      case FindAllCommand =>
        log.info("receive find all command.")
        sender() ! Seq(TodoReply(1, "test1"), TodoReply(2, "test2"))
      case FindByIdCommand(id) =>
        log.info("receive find by id command.")
        sender() ! Some(TodoReply(id, "test"))
      case _: CreateCommand =>
        log.info("receive create command.")
        sender() ! CreatedReply(1)
      case _: UpdateCommand =>
        log.info("receive update command.")
        sender() ! UpdatedReply
      case _: DeleteCommand =>
        log.info("receive delete command.")
        sender() ! DeletedReply
    }
  }))

  val todoRoutes = TodoRoutes(actorMock)

  "todo routes" should {

    "find all" in {
      Get("/todos") ~> todoRoutes.routes ~> check {
        responseAs[Seq[TodoReply]] shouldBe Seq(TodoReply(1, "test1"), TodoReply(2, "test2"))
      }
    }

    "find by id" in {
      Get("/todos/1") ~> todoRoutes.routes ~> check {
        responseAs[TodoReply] shouldBe TodoReply(1, "test")
      }
    }

    "create" in {
      Post("/todos", HttpEntity(ContentTypes.`application/json`, """{ "body": "test" }""")) ~> todoRoutes.routes ~> check {
        responseAs[CreatedReply] shouldBe CreatedReply(1)
      }
    }

    "update" in {
      Put(
        "/todos/1",
        HttpEntity(ContentTypes.`application/json`, """{ "id": 1, "body": "test" }""")) ~> todoRoutes.routes ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }

    "delete" in {
      Delete("/todos/1") ~> todoRoutes.routes ~> check {
        status shouldBe StatusCodes.NoContent
      }
    }
  }
}
