package com.cham.dao

import com.cham.core.Keyspaces

import akka.actor.Actor
import com.datastax.driver.core.Cluster
import com.cham.domain.Customer

/**
  * Created by cwijayasundara on 19/12/2016.
  */
class CustomerWriterActor(cluster: Cluster) extends Actor{

  val session = cluster.connect(Keyspaces.webshop)

  val preparedStatement = session.prepare("INSERT INTO customers(customerid, customername, emailaddress, deliveryaddress,orderid,createddatetime) VALUES (?, ?, ?, ?, ?, ?);")

  def saveCustomer(customer: Customer): Unit =
    session.executeAsync(preparedStatement.bind(customer.customerId.id, customer.customerName.text, customer.emailAddress.text,
                                                customer.deliveryAddress.text, customer.orderId.id, customer.createdDateTime))

  def receive: Receive = {

    case customer: Customer => saveCustomer(customer)

    case customers: Vector[Customer] => {
      customers.foreach(saveCustomer)}
  }

}
