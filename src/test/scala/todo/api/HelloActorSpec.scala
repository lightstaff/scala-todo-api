package todo.api

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class HelloActorSpec
    extends TestKit(ActorSystem("hello-actor-spec"))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  import HelloActor._

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "hello actor" should {

    "hello!!" in {
      val probe = TestProbe()
      val helloActor = system.actorOf(Props[HelloActor])

      helloActor.tell(HelloCommand("ME"), probe.ref)
      probe.expectMsg(HelloReply("Hello ME!!"))
    }
  }

}
