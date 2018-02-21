package example

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.ThrottleMode

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object PingPong extends App {
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  trait Message
  case class Ping(id: Int) extends Message
  case class Pong(id: Int) extends Message

  def pass(msg: Message): Message = {
    msg match {
      case Ping(id) => Ping(id)
      case Pong(id) => Pong(id)
    }
  }

  val codec = BidiFlow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val outbound = b.add(Flow[Message].map(pass))
    val inbound = b.add(Flow[Message].map(pass))

    BidiShape.fromFlows(outbound, inbound)
  })

  val pingpong = Flow[Message]
    .mapAsync(1) {
    case Ping(id) => Future {
      Thread.sleep(100)
      Pong(id)
    }
  }

  val flow = codec.join(pingpong)

  val result = Source((0 to 9).map(Ping)).via(flow).map(println).runWith(Sink.seq)
  Await.result(result.andThen {case _ => system.terminate()}, Duration.Inf)
}
