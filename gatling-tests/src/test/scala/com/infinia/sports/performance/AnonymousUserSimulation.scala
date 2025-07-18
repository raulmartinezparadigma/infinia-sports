package com.infinia.sports.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class AnonymousUserSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling/3.9.5")

  // Feeder para los términos de búsqueda
  val searchFeeder = csv("search_terms.csv").random

  // Escenario que simula a un usuario navegando por la web
  val scn = scenario("Anonymous User Journey")
    // 1. Cargar el catálogo de productos y guardar todos los IDs
    .exec(http("Load Product Catalog")
      .get("/api/products")
      .check(status.is(200))
      .check(jsonPath("$[*].id").findAll.saveAs("productIds")))
    .pause(1.second, 3.seconds) // Pausa realista

    // 2. Usar un término del feeder para buscar un producto
    .feed(searchFeeder)
    .exec(http("Search for a Product")
      .get("/api/products?query=${searchTerm}")
      .check(status.is(200)))
    .pause(1.second, 3.seconds)

    // 3. Seleccionar un ID de producto aleatorio de la lista guardada
    .exec(session => {
      val productIds = session("productIds").as[Vector[String]]
      val randomProductId = productIds(Random.nextInt(productIds.size))
      session.set("productId", randomProductId)
    })

    // 4. Ver los detalles de ese producto específico
    .exec(http("View Product Details")
      .get("/api/products/${productId}")
      .check(status.is(200)))

  // Configuración de la carga: 10 usuarios en 20 segundos
  setUp(
    scn.inject(rampUsers(10).during(20.seconds))
  ).protocols(httpProtocol)
}
