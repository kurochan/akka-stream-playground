package example

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Hello extends App {
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val result = Source((0 to 9).map(n => s"hello: ${n}"))
  .map { msg =>
    Thread.sleep(100)
    println(msg)
  }.runWith(Sink.seq)
  Await.result(result.andThen {case _ => system.terminate()}, Duration.Inf)
}
