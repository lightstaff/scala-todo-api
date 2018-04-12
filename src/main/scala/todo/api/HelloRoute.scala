package todo.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object HelloRoute {

  // nameパラメータを受け付けテキストを返信する
  val helloRoute: Route = path("hello") {
    parameter('name) { name =>
      complete(StatusCodes.OK -> s"Hello $name!!")
    }
  }

}
