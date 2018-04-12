package todo.api

import scala.concurrent.{ExecutionContext, Future}

import slick.jdbc.H2Profile.api._

object HelloSlick {

  def apply(db: Database)(implicit ec: ExecutionContext): HelloSlick = new HelloSlick(db)

  // モデル定義
  final case class Hello(id: Int, name: String)

  // テーブル定義
  class HelloTable(tag: Tag) extends Table[Hello](tag, "hello") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    override def * = (id, name) <> (Hello.tupled, Hello.unapply)

  }

  val helloTable = TableQuery[HelloTable]

}

class HelloSlick(db: Database)(implicit ec: ExecutionContext) {

  import HelloSlick._

  // 全件取得
  def findAll(): Future[Seq[Hello]] = db.run(helloTable.result)

  // Id指定取得
  def findById(id: Int): Future[Option[Hello]] =
    db.run(helloTable.filter(_.id === id).result.headOption)

  // 追加
  def create(hello: Hello): Future[Hello] =
    db.run(helloTable returning helloTable.map(_.id) += hello).map(id => hello.copy(id = id))

  // 更新
  def update(hello: Hello): Future[Int] =
    db.run(helloTable.filter(_.id === hello.id).map(_.name).update(hello.name))

  // 削除
  def delete(id: Int): Future[Int] = db.run(helloTable.filter(_.id === id).delete)

}
