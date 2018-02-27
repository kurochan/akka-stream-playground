package example

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FlatMapConcat extends App {
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val input: Seq[Either[String, Int]] = Seq(
    Right(1),
    Left("error1"),
    Left("error2"),
    Left("error3"),
    Left("error4"),
    Left("error5"),
    Right(2),
    Right(3),
    Right(4),
    Right(5),
    Left("error6"),
    Right(6),
    Right(7),
    Right(8),
    Left("error2"),
    Right(9)
  )

  val result = Source.fromIterator(() => input.toIterator)
      .map { msg => println(s"##### INPUT: ${msg} #####"); msg }
      .flatMapConcat({
        case Right(n) => Source(List(n))
          .map { msg => println(s"!!! Right: ${msg} !!!"); Right(msg) }
          .throttle(1, 3.second, 0, ThrottleMode.shaping)
        case Left(n) =>  Source(List(n)).map(Left(_))
      })
      .map { msg => println(s"##### OUTPUT: ${msg} #####"); msg }
      .runWith(Sink.ignore)

  Await.result(result.andThen {case _ => system.terminate()}, Duration.Inf)
}
