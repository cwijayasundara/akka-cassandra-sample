package com.cham.domain

/**
  * Created by cwijayasundara on 12/12/2016.
  */

import java.util.Date

case class Text(text: String) extends AnyVal

object Text {
  implicit def toText(text: String): Text = Text(text)
}

case class CustomerId(id: String) extends AnyVal

object CustomerId {
  implicit def toTweetId(id: String): CustomerId = CustomerId(id)
}

case class OrderId(id:String) extends AnyVal

object OrderId{
  implicit def toOrderId(id:String): OrderId = OrderId(id)
}

case class Customer(customerId: CustomerId, customerName:Text,emailAddress: Text, deliveryAddress: Text, orderId : OrderId, createdDateTime: Date)