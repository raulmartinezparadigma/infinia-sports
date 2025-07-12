package com.infinia.sports.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080") // Asegúrate de que este es el puerto correcto de tu aplicación Spring Boot
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling/3.9.5")

  val scn = scenario("Get All Products")
    .exec(http("request_products")
      .get("/api/products"))
    .pause(1)

  setUp(
    scn.inject(atOnceUsers(700)) // Simula 10 usuarios concurrentes
  ).protocols(httpProtocol)
}
