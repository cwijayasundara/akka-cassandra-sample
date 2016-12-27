package com.cham.service

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
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
  case class CreateCustomer(customer:Customer)
  case class CreateCustomers(customers: Vector[Customer])
  case class GetAllCustomers(limit:Int) // max =100
  case class GetCustomer(custId:String)
  case class GetCustomerCount()
  case class CustomerResponse(customer:Customer)
  case class Customers(customers: Vector[Customer])
  case class CreateCustomersResponse(customers: Vector[Customer])

}

class CustomerServiceActor(system:ActorSystem,cluster:Cluster) extends Actor{

  val customerWriter = system.actorOf(Props(new CustomerWriterActor(cluster)))
  val customerReader = system.actorOf(Props(new CustomerReaderActor(cluster)))


  // need to handle the futures and responses to REST layer..

  def receive: Receive = {

    case CreateCustomer(customer:Customer) => customerWriter ! CustomerWriterActor.CreateCustomer(customer)
    case CreateCustomers(customers:Vector[Customer]) => customerWriter ! CustomerWriterActor.CreateCustomers(customers)

    case GetAllCustomers(limit:Int) => //customerReader ! CustomerReaderActor.FindAll(limit)
      {
        def notFound() = sender() ! None
        def getAllCustomers(child:ActorRef) = child forward CustomerReaderActor.FindAll(limit)
        context.child("customers").fold(notFound())(getAllCustomers)
      }

    case GetCustomerCount =>
    {
      def notFound() = sender() ! None
      def getCustomerCount(child: ActorRef) = child forward CustomerReaderActor.CountAll
      context.child("count").fold(notFound())(getCustomerCount)
    }
    case GetCustomer(custName) => {
      def notFound() = sender() ! None
      def getCustomer(child: ActorRef) = child forward CustomerReaderActor.FindCustomer(custName)
      context.child(custName).fold(notFound())(getCustomer)
    }

  }
}
