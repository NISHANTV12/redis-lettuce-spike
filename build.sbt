name := "RedisLettuceSpike"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "io.lettuce" % "lettuce-core" % "5.0.1.RELEASE"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.8"
libraryDependencies += "org.scala-lang.modules" % "scala-java8-compat_2.12" % "0.8.0"