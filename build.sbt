name := "akka-cassandra"

version := "1.0"

scalaVersion := "2.11.8"

organization := "com.cham"

resolvers += "OSS Sonatype" at "https://repo1.maven.org/maven2/"

libraryDependencies ++= {
  val akkaVersion = "2.4.9"
  val sprayVersion = "1.3.1"
  Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion withSources(),
    "com.typesafe.akka" % "akka-http-core_2.11" % akkaVersion withSources(),
    "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaVersion withSources(),
    "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % akkaVersion withSources(),
    "com.typesafe.akka" % "akka-slf4j_2.11" % akkaVersion withSources(),
    "ch.qos.logback" % "logback-classic" % "1.1.3" withSources(),
    "com.datastax.cassandra"  % "cassandra-driver-core" % "3.0.0"  exclude("org.xerial.snappy", "snappy-java"),
    "io.spray" % "spray-can_2.11" % "1.3.1" withSources(),
    "io.spray" % "spray-client_2.11" % "1.3.1" withSources(),
    "io.spray" % "spray-routing_2.11" % "1.3.1" withSources(),
    "io.spray" % "spray-json_2.11" % "1.3.1" withSources(),
    "io.spray" % "spray-testkit_2.11" % "1.3.1" withSources(),
    "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.9" withSources(),
    "com.novocode" % "junit-interface" % "0.11" withSources(),
    "org.xerial.snappy" % "snappy-java" % "1.1.1.3" withSources(),
    "org.specs2" % "specs2-core_2.11" % "2.4.6" withSources()

  )

}
    