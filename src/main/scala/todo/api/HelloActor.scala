package todo.api

import akka.actor.{Actor, ActorLogging}

object HelloActor {

  // 受信用メッセージ
  final case class HelloCommand(name: String)

  // 返信用メッセージ
  final case class HelloReply(message: String)

}

class HelloActor() extends Actor with ActorLogging {

  import HelloActor._

  override def preStart(): Unit = log.info("starting hello actor.")

  override def postStop(): Unit = log.info("stopping hello actor.")

  // HelloCommandを受け取ったらHelloReplyを返す
  override def receive: Receive = {
    case HelloCommand(name) =>
      sender() ! HelloReply(s"Hello $name!!")
  }

}
