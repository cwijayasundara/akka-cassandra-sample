package com.cham

import akka.actor.{ActorSystem, Props}
import com.cham.cassandrautil.ConfigCassandraCluster
import com.cham.domain.Customer
import java.util.Date

import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.cham.rest.WebShopRestApi
import com.cham.service.{CustomerService, CustomerServiceActor}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future

/**
  * Created by cwijayasundara on 19/12/2016.
  */

object Main extends App with ConfigCassandraCluster with RequestTimeOut {

  // load the configs from application.config
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  // create the actor system and dispatcher. Actor system creates a non demon thread that keeps running
  implicit lazy val system = ActorSystem()
  implicit val dispatcher = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val webShopApi = new WebShopRestApi(system, requestTimeout(config),cluster).routes

  // start the http server
  val webShopFuture: Future[ServerBinding] =  Http().bindAndHandle(webShopApi, host, port)

  val customerServiceActor = system.actorOf(Props(new CustomerServiceActor(system,cluster)))

  val log =  Logging(system.eventStream, "webshop")

  webShopFuture.map { binding =>
    log.info(s"RestApi bound to ${binding.localAddress} ")
  }.onFailure {
    case ex: Exception =>
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }
}

trait RequestTimeOut {
  import scala.concurrent.duration._

  def requestTimeout(config: Config): Timeout = {
  val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}