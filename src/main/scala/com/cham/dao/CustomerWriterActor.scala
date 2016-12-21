package com.cham.dao

import com.cham.core.Keyspaces
import akka.actor.Actor
import com.cham.dao.CustomerWriterActor.{CreateCustomer, CreateCustomers}
import com.datastax.driver.core.Cluster
import com.cham.domain.Customer

/**
  * Created by cwijayasundara on 19/12/2016.
  */

object CustomerWriterActor{

  case class CreateCustomer(customer:Customer)
  case class CreateCustomers(customers:Vector[Customer])

}
class CustomerWriterActor(cluster: Cluster) extends Actor{

  val session = cluster.connect(Keyspaces.webshop)

  val insertSql = "INSERT INTO customers(customerid, customername, emailaddress, deliveryaddress,orderid,createddatetime) VALUES (?, ?, ?, ?, ?, ?);"

  val preparedStatement = session.prepare(insertSql)

  def saveCustomer(customer: Customer): Unit = {
    printf("Inside saveCustomer() of CustomerWriterActor")
    session.executeAsync(preparedStatement.bind(customer.customerId.id, customer.customerName.text, customer.emailAddress.text,
      customer.deliveryAddress.text, customer.orderId.id, customer.createdDateTime))
  }

  def receive: Receive = {

    case CreateCustomer(customer:Customer) => saveCustomer(customer)

    case CreateCustomers(customers:Vector[Customer]) => {
      customers.foreach(saveCustomer)}
  }

}
