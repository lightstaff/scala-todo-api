package todo.api.actor

import scala.concurrent.Future

import akka.actor.{ActorRef, ActorSystem, Status}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scalaz.\/
import scalaz.syntax.ToEitherOps

import todo.api.repository.{Model, TodoRepository}

class TodoActorSpec
    extends TestKit(ActorSystem("todo-actor-spec"))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  import TodoActor._
  import Model._

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  trait SuccessPattern {

    val mockRepo: TodoRepository = new TodoRepository with ToEitherOps {
      override def findAll(): Future[\/[Throwable, Seq[Todo]]] =
        Future.successful(Seq(Todo(1, "test1"), Todo(2, "test2")).right[Throwable])

      override def findById(id: Int): Future[\/[Throwable, Option[Todo]]] =
        Future.successful(Some(Todo(id, "test")).right[Throwable])

      override def create(data: Todo): Future[\/[Throwable, Int]] =
        Future.successful(1.right[Throwable])

      override def update(data: Todo): Future[\/[Throwable, Int]] =
        Future.successful(1.right[Throwable])

      override def delete(id: Int): Future[\/[Throwable, Int]] =
        Future.successful(1.right[Throwable])
    }

    val probe = TestProbe()
    val todoActor: ActorRef = system.actorOf(TodoActor.props(mockRepo))

  }

  trait FailPattern {

    val error = new Exception("raised error!!")

    val mockRepo: TodoRepository = new TodoRepository with ToEitherOps {
      override def findAll(): Future[\/[Throwable, Seq[Todo]]] =
        Future.successful(error.left[Seq[Todo]])

      override def findById(id: Int): Future[\/[Throwable, Option[Todo]]] =
        Future.successful(error.left[Option[Todo]])

      override def create(data: Todo): Future[\/[Throwable, Int]] =
        Future.successful(error.left[Int])

      override def update(data: Todo): Future[\/[Throwable, Int]] =
        Future.successful(error.left[Int])

      override def delete(id: Int): Future[\/[Throwable, Int]] =
        Future.successful(error.left[Int])
    }

    val probe = TestProbe()
    val todoActor: ActorRef = system.actorOf(TodoActor.props(mockRepo))

  }

  "todo actor" should {

    "success pattern" when {
      "find all" in new SuccessPattern {
        todoActor.tell(FindAllCommand, probe.ref)

        probe.expectMsg(Seq(TodoReply(1, "test1"), TodoReply(2, "test2")))
      }

      "find by id" in new SuccessPattern {
        todoActor.tell(FindByIdCommand(1), probe.ref)

        probe.expectMsg(Some(TodoReply(1, "test")))
      }

      "create" in new SuccessPattern {
        todoActor.tell(CreateCommand("test"), probe.ref)

        probe.expectMsg(CreatedReply(1))
      }

      "update" in new SuccessPattern {
        todoActor.tell(UpdateCommand(1, "test1"), probe.ref)

        probe.expectMsg(UpdatedReply)
      }

      "delete" in new SuccessPattern {
        todoActor.tell(DeleteCommand(1), probe.ref)

        probe.expectMsg(DeletedReply)
      }
    }

    "fail pattern" when {

      "find all" in new FailPattern {
        todoActor.tell(FindAllCommand, probe.ref)

        probe.expectMsg(Status.Failure(error))
      }

      "find by id" in new FailPattern {
        todoActor.tell(FindByIdCommand(1), probe.ref)

        probe.expectMsg(Status.Failure(error))
      }

      "create" in new FailPattern {
        todoActor.tell(CreateCommand("test"), probe.ref)

        probe.expectMsg(Status.Failure(error))
      }

      "update" in new FailPattern {
        todoActor.tell(UpdateCommand(1, "test1"), probe.ref)

        probe.expectMsg(Status.Failure(error))
      }

      "delete" in new FailPattern {
        todoActor.tell(DeleteCommand(1), probe.ref)

        probe.expectMsg(Status.Failure(error))
      }
    }
  }
}
