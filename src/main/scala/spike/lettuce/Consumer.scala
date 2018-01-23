package spike.lettuce

import akka.actor.ActorSystem
import akka.stream._
import io.lettuce.core.RedisClient

object Consumer extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  private val actorMaterializerSettings = ActorMaterializerSettings(actorSystem).withInputBuffer(1, 1)
  implicit val mat: ActorMaterializer = ActorMaterializer(actorMaterializerSettings)(actorSystem)

  val client: RedisClient = RedisClient.create("redis://localhost")

//  new LettuceConsumer(client).run.subscribe()

//  new LettuceConsumer(client).slowConsumer

  new LettuceConsumer(client).throttle
}


