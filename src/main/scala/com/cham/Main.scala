package com.cham

import akka.actor.{ActorSystem, Props}
import com.cham.cassandrautil.ConfigCassandraCluster
import com.cham.dao.CustomerReaderActor
import com.cham.dao.CustomerWriterActor
import com.cham.domain.Customer
import java.util.Date

import com.cham.dao.CustomerReaderActor.{CountAll, FindAll}

/**
  * Created by cwijayasundara on 19/12/2016.
  */
object Main extends App with ConfigCassandraCluster {

  implicit lazy val system = ActorSystem()

  val write = system.actorOf(Props(new CustomerWriterActor(cluster)))
  val read = system.actorOf(Props(new CustomerReaderActor(cluster)))

  val testCustomer1:Customer = new Customer("4", "test customer","test@gmail.com"," some address","4444", new Date())

  write ! testCustomer1
  read ! FindAll(100)
  read ! CountAll

}
