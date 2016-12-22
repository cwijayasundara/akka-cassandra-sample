package com.cham.rest

import com.cham.service.CustomerService.{CustomerResponse, Customers}
import spray.json._

case class EventDescription(customers: Int) {
  require(customers > 0)
}

case class CustomerRequest(customers: Int) {
  require(customers > 0)
}

case class Error(message: String)

trait EventMarshalling  extends DefaultJsonProtocol {

  //implicit val jsonCustomerFormat = jsonFormat1(CustomerResponse)
  //implicit val jsonCustomersFormat = jsonFormat1(Customers)
}
