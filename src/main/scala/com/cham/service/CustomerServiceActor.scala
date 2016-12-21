package com.cham.service

import akka.actor.{Actor, ActorSystem, Props}
import com.cham.dao.{CustomerReaderActor, CustomerWriterActor}
import com.cham.domain.Customer
import com.cham.service.CustomerService._
import com.datastax.driver.core.Cluster


/**
  * Created by cwijayasundara on 20/12/2016.
  * This is the service facade for the customerDaos
  */

object CustomerService{
  // define the actors messages
  case class CreateCustomerEvent(customer:Customer)
  case class CreateCustomersEvent(customers: Vector[Customer])
  case class GetAllCustomers(limit:Int) // max =100
  case class GetCustomer(custId:String)
  case class GetCustomerCount()
  case class CustomerResponse(customer:Customer)
  case class Customers(customers: Vector[Customer])

}

class CustomerServiceActor(system:ActorSystem,cluster:Cluster) extends Actor{

  val customerWriter = system.actorOf(Props(new CustomerWriterActor(cluster)))
  val customerReader = system.actorOf(Props(new CustomerReaderActor(cluster)))

  def receive: Receive = {

    case CreateCustomerEvent(customer:Customer) => customerWriter ! CustomerWriterActor.CreateCustomer(customer)
    case CreateCustomersEvent(customers:Vector[Customer]) => customerWriter ! CustomerWriterActor.CreateCustomers(customers)
    case GetAllCustomers(limit:Int) => customerReader ! CustomerReaderActor.FindAll(limit)
    case GetCustomerCount => customerReader ! CustomerReaderActor.CountAll
    case GetCustomer(custName:String) => customerReader ! CustomerReaderActor.FindCustomer(custName)

  }
}
