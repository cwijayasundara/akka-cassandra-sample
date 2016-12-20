package com.cham.cassandrautil

import akka.actor.ActorSystem
import com.datastax.driver.core.{Cluster, ProtocolOptions}

/**
  * Created by cwijayasundara on 19/12/2016.
  */
trait CassandraCluster {
  def cluster: Cluster
}

trait ConfigCassandraCluster extends CassandraCluster {

  def system: ActorSystem

  private def config = system.settings.config

  import scala.collection.JavaConversions._

  private val cassandraConfig = config.getConfig("akka-cassandra.main.db.cassandra")
  private val port = cassandraConfig.getInt("port")
  private val hosts = cassandraConfig.getStringList("hosts").toList

  lazy val cluster: Cluster =
    Cluster.builder().
      addContactPoints(hosts: _*).
      withCompression(ProtocolOptions.Compression.SNAPPY).
      withPort(port).
      build()
}
