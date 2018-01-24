package spike.lettuce

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.{Sink, Source}
import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands

import scala.compat.java8._
import scala.concurrent.duration.DurationDouble

object Publisher extends App {

  implicit val actorSystem = ActorSystem()
  implicit val mat = ActorMaterializer()


  val client: RedisClient = RedisClient.create("redis://localhost")
  client.connect().async()

  val connection: StatefulRedisPubSubConnection[String, String] = client.connectPubSub
  val pubSubCommands: RedisPubSubAsyncCommands[String, String] = connection.async()

  Source(1 to 500000)
    //        .tick(1.second, 1.millis, ())
    //    .repeat(())
    .mapAsync(1) { x ⇒
    println(x)
    FutureConverters.toScala(pubSubCommands.publish("channel", x.toString))
  }
    .throttle(100, 1.second, 100, ThrottleMode.shaping)
    .runWith(Sink.ignore)


}
