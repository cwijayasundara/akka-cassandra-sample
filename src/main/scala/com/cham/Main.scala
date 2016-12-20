package com.cham

import akka.actor.{ActorSystem, Props}
import com.cham.cassandrautil.ConfigCassandraCluster
import com.cham.dao.CustomerReaderActor
import com.cham.dao.CustomerWriterActor
import com.cham.domain.Customer

/**
  * Created by cwijayasundara on 19/12/2016.
  */
object Main extends App with ConfigCassandraCluster {

  import akka.actor.ActorDSL._

  implicit lazy val system = ActorSystem()

  val write = system.actorOf(Props(new CustomerWriterActor(cluster)))
  val read = system.actorOf(Props(new CustomerReaderActor(cluster)))

  val testCustomer1:Customer = new Customer("cust1", "chaminda w","cwijayasundara@gmail.com","14 wentworth close uk","webshop123", new java.util.Date())

  write ! testCustomer1
  read ! CustomerReaderActor.FindAll
  read ! CustomerReaderActor.CountAll

  system.shutdown()

}
