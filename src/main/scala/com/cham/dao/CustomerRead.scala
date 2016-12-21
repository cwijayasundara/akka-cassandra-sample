package com.cham.dao

/**
  * Created by cwijayasundara on 12/12/2016.
  */

import java.text.SimpleDateFormat
import java.util.Date

import com.cham.core.Keyspaces
import com.cham.dao.CustomerReaderActor.{CountAll, FindAll, FindCustomer}
import akka.actor.Actor
import com.cham.domain.Customer
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.{BoundStatement, Cluster, Row}

// companion object
object CustomerReaderActor {
  case class FindAll(maximum: Int = 100)
  case class FindCustomer(custName:String)
  case object CountAll
}

class CustomerReaderActor(cluster: Cluster) extends Actor {

  val session = cluster.connect(Keyspaces.webshop)
  val countAll  = new BoundStatement(session.prepare("select count(*) from customers;"))
  // need to remove the hard coding and accept the custid as a param
  val findCustomerById = new BoundStatement(session.prepare("select * from customers where customerid ='1'"))

  import scala.collection.JavaConversions._
  import com.cham.cassandrautil.cassandra.resultset._
  import context.dispatcher
  import akka.pattern.pipe

  def buildCustomer(r: Row): Customer = {
    val customerId = r.getString("customerid")
    val customerName = r.getString("customername")
    val emailAddress = r.getString("emailaddress")
    val deliveryAddress = r.getString("deliveryaddress")
    val orderId = r.getString("orderid")
    val createdAt = r.getDate("createddatetime")
    val date : Date = new SimpleDateFormat("yyyy-MM-dd").parse(createdAt.toString())
    Customer(customerId, customerName, emailAddress, deliveryAddress,orderId,date)
  }

  def receive: Receive = {

    case FindAll(maximum:Int)  => {
      println("Inside the FindAll() of the CustomerReaderActor..")
      val query = QueryBuilder.select().all().from(Keyspaces.webshop, "customers").limit(maximum)
      session.executeAsync(query) map (_.all().map(buildCustomer).toVector) pipeTo sender
    }

    case CountAll => {
      println("Inside the CountAll() of the CustomerReaderActor..")
      session.executeAsync(countAll) map (_.one.getLong(0)) pipeTo sender
    }

    case FindCustomer(custName:String) => {
      println("Inside the FindCustomer() of the CustomerReaderActor..")
      session.executeAsync(findCustomerById) map (_.one.getLong(0)) pipeTo sender
    }
  }
}
