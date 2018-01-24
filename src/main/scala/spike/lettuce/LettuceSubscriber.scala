package spike.lettuce

import akka.Done
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{DelayOverflowStrategy, Materializer}
import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.reactive.{ChannelMessage, RedisPubSubReactiveCommands}
import reactor.core.publisher.{Flux, FluxSink}

import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble

class LettuceSubscriber(client: RedisClient)(implicit mat: Materializer) {

  val connection: StatefulRedisPubSubConnection[String, String] = client.connectPubSub
  val reactive: RedisPubSubReactiveCommands[String, String] = connection.reactive

  reactive.subscribe("channel").subscribe

  def throttle: Future[Done] =
    Source.fromPublisher(reactive.observeChannels(FluxSink.OverflowStrategy.LATEST)).async
      .delay(1.second, DelayOverflowStrategy.dropHead)
      .runForeach { x ⇒
        println(System.currentTimeMillis() - x.getMessage.toLong)
      }

  def slowConsumer: Future[Done] =
    Source.fromPublisher(reactive.observeChannels(FluxSink.OverflowStrategy.LATEST)).async
      .runForeach { x ⇒
        println(System.currentTimeMillis() - x.getMessage.toLong)
        Thread.sleep(10000)
      }

  def consumer: Future[immutable.Seq[Long]] =
    Source.fromPublisher(reactive.observeChannels(FluxSink.OverflowStrategy.LATEST)).async
      .take(2000)
        .map(_.getMessage.toLong)
        .runWith(Sink.seq)

  def run: Flux[Unit] = reactive.observeChannels(FluxSink.OverflowStrategy.LATEST).map[Unit] { x ⇒
    Thread.sleep(1000)
    println(System.currentTimeMillis() - x.getMessage.toLong)
  }
}
