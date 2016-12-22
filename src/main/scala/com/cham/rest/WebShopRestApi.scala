package com.cham.rest

import akka.actor.{ActorRef, ActorSystem, Props}

import scala.concurrent.ExecutionContext
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.cham.service.CustomerService._
import com.cham.service.CustomerServiceActor
import com.cham.domain.Customer
import com.datastax.driver.core.Cluster

class WebShopRestApi(system: ActorSystem, timeout: Timeout,cluster: Cluster) extends RestRoutes {

  implicit val requestTimeout = timeout
  implicit def executionContext = system.dispatcher

  val createCustomerServiceActor = system.actorOf(Props(new CustomerServiceActor(system,cluster)))
}

trait RestRoutes extends CustomerServiceApi with EventMarshalling {

  import StatusCodes._

  def routes: Route = customersRoute ~ customerRoute

  // host:port/webshop/customer
  def customersRoute =
    pathPrefix("customers") {
      pathEndOrSingleSlash {
        get {
          // GET /customers
          onSuccess(getCustomers(100)) { customers: Customers =>
            println("Inside the WebShopRestApi .." + customers.toString)
            complete(OK)
          }
        }
      }
    }

  def customerRoute =
    pathPrefix("customer") {
      pathEndOrSingleSlash {
        get {
          // GET /customer
          onSuccess(getCustomer("1")) { customer: Customer =>
            complete(OK)
          }
        }
      }
    }

}


// This trait has the linking between the REST API and the actor facade..
trait CustomerServiceApi {

  def createCustomerServiceActor(): ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val customerServiceActor = createCustomerServiceActor()

  def createCustomer(customer:Customer) = {
  customerServiceActor.ask(CreateCustomer(customer)).mapTo[CustomerResponse]
  }
  def createCustomers(customers:Vector[Customer]) = {
    customerServiceActor.ask(CreateCustomers(customers)).mapTo[CreateCustomersResponse]
  }
  def getCustomers(limit:Int)={
    customerServiceActor.ask(GetAllCustomers(limit)).mapTo[Customers]
  }
  def getCustomer(customerId:String) = {
    customerServiceActor.ask(GetCustomer(customerId)).mapTo[Customer]
  }
  def getCustomerCount() = {
    customerServiceActor.ask(GetCustomerCount)
  }
}
