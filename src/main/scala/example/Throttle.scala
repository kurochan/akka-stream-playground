package example

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.ThrottleMode

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Throttle extends App {
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  val throttle = Flow[Int].throttle(1, 1.second, 0, ThrottleMode.shaping)
  val result = Source((0 to 9)).via(throttle).via(throttle).map(println).runWith(Sink.seq)
  Await.result(result.andThen {case _ => system.terminate()}, Duration.Inf)
}
