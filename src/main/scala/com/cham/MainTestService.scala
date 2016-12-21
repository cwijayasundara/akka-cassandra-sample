package com.cham

import akka.actor.{ActorSystem, Props}
import com.cham.cassandrautil.ConfigCassandraCluster
import com.cham.domain.Customer
import java.util.Date

import akka.event.Logging
import akka.stream.ActorMaterializer
import com.cham.service.{CustomerService, CustomerServiceActor}
import com.typesafe.config.{ConfigFactory}


/**
  * Created by cwijayasundara on 19/12/2016.
  * Temp class to test the service facade actor
  */

object Main extends App with ConfigCassandraCluster {

  // load the configs from application.config
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  // create the actor system and dispatcher. Actor system creates a non demon thread that keeps running
  implicit lazy val system = ActorSystem()
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val customerServiceActor = system.actorOf(Props(new CustomerServiceActor(system,cluster)))

  val log =  Logging(system.eventStream, "webshop")

  val testCustomer:Customer = new Customer("5", "test customer","test@gmail.com"," some address","5555", new Date())

  customerServiceActor ! CustomerService.CreateCustomerEvent(testCustomer)
  customerServiceActor ! CustomerService.GetAllCustomers
  customerServiceActor ! CustomerService.GetCustomerCount
  customerServiceActor ! CustomerService.GetCustomer("1")

}
