package spike.lettuce

import akka.actor.ActorSystem
import akka.stream._
import io.lettuce.core.RedisClient

import scala.collection.immutable
import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

object Subscriber extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  private val actorMaterializerSettings = ActorMaterializerSettings(actorSystem).withInputBuffer(1, 1)
  implicit val mat: ActorMaterializer = ActorMaterializer(actorMaterializerSettings)(actorSystem)

  val client: RedisClient = RedisClient.create("redis://localhost")

//  new LettuceConsumer(client).run.subscribe()

//  new LettuceSubscriber(client).slowConsumer

  val consumer: immutable.Seq[Long] = Await.result(new LettuceSubscriber(client).consumer, 30.seconds)

  println(consumer == consumer.sorted)

  println(consumer)
  println(consumer.sorted)



//  new LettuceConsumer(client).throttle
}


