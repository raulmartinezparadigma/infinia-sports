package com.infinia.sports.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.util.Random

/**
 * Simulación de Gatling para probar las estrategias de Resilience4j.
 * 
 * Esta prueba estresa específicamente los endpoints protegidos con:
 * - Circuit Breaker (mongoService)
 * - Retry (mongoService)
 * - Fallback methods
 * 
 * Objetivo: Demostrar cómo Resilience4j protege la aplicación bajo carga extrema.
 */
class Resilience4jStressSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling-Resilience4j-Test/3.9.5")

  val userFeeder = csv("users.csv").circular

  // ============================================
  // ESCENARIO 1: Estrés en GET /api/cart
  // ============================================
  // Este endpoint está protegido con @CircuitBreaker y @Retry
  // Objetivo: Activar el Circuit Breaker tras múltiples fallos
  val cartStressScenario = scenario("Cart Service Stress - Circuit Breaker Test")
    .feed(userFeeder)
    .exec(http("User Login")
      .post("/api/auth/login")
      .body(StringBody("""{"username": "${username}", "password": "${password}"}"""))
      .asJson
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("authToken")))
    .pause(500.milliseconds)
    .repeat(50, "cartRequestCount") {
      exec(http("Get Cart - Protected by Circuit Breaker")
        .get("/api/cart")
        .header("Authorization", "Bearer ${authToken}")
        .check(status.in(200, 500, 503))) // Aceptamos fallos esperados
        .pause(100.milliseconds, 300.milliseconds)
    }

  // ============================================
  // ESCENARIO 2: Carga normal + operaciones de carrito
  // ============================================
  // Simula usuarios agregando productos al carrito bajo carga
  val normalLoadScenario = scenario("Normal Load - Add to Cart Operations")
    .exec(http("Load Product Catalog")
      .get("/api/products")
      .check(status.is(200))
      .check(bodyString.saveAs("productsJsonString")))
    .pause(1.second)
    .feed(userFeeder)
    .exec(http("User Login")
      .post("/api/auth/login")
      .body(StringBody("""{"username": "${username}", "password": "${password}"}"""))
      .asJson
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("authToken")))
    .pause(500.milliseconds)
    .exec(session => {
      val jsonString = session("productsJsonString").as[String]
      val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
      val products = mapper.readValue(jsonString, classOf[List[Map[String, Any]]])
      if (products.nonEmpty) {
        val randomProduct = products(Random.nextInt(products.size))
        session.set("productId", randomProduct("id"))
               .set("productName", randomProduct("description"))
               .set("description", randomProduct("description"))
               .set("unitPrice", randomProduct("price"))
               .set("productImageUrl", randomProduct("imageUrl"))
      } else {
        session
      }
    })
    .exec(http("Add to Cart")
      .post("/api/cart/items")
      .header("Authorization", "Bearer ${authToken}")
      .body(StringBody("""{
        "productId": "${productId}",
        "productName": "${productName}",
        "description": "${description}",
        "quantity": 1,
        "unitPrice": ${unitPrice},
        "productImageUrl": "${productImageUrl}",
        "attributes": {}
      }"""))
      .asJson
      .check(status.in(200, 500)))
    .pause(1.second)
    .exec(http("View Cart")
      .get("/api/cart")
      .header("Authorization", "Bearer ${authToken}")
      .check(status.in(200, 500)))

  // ============================================
  // ESCENARIO 3: Ráfaga de peticiones concurrentes
  // ============================================
  // Simula una ráfaga súbita para probar el Rate Limiter (si está configurado)
  val burstScenario = scenario("Burst Load - Rate Limiter Test")
    .feed(userFeeder)
    .exec(http("User Login")
      .post("/api/auth/login")
      .body(StringBody("""{"username": "${username}", "password": "${password}"}"""))
      .asJson
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("authToken")))
    .repeat(20, "burstCount") {
      exec(http("Get Cart - Burst Request")
        .get("/api/cart")
        .header("Authorization", "Bearer ${authToken}")
        .check(status.in(200, 429, 500, 503)))
    }

  // ============================================
  // CONFIGURACIÓN DE CARGA
  // ============================================
  setUp(
    // Escenario 1: Estrés intenso para activar Circuit Breaker
    // 20 usuarios concurrentes durante 30 segundos
    cartStressScenario.inject(
      rampUsers(20).during(10.seconds),
      constantUsersPerSec(10).during(20.seconds)
    ),

    // Escenario 2: Carga normal en background
    // Usuarios graduales para simular tráfico real
    normalLoadScenario.inject(
      rampUsers(10).during(30.seconds)
    ),

    // Escenario 3: Ráfaga súbita después de 20 segundos
    // Simula un pico de tráfico repentino
    burstScenario.inject(
      nothingFor(20.seconds),
      atOnceUsers(30)
    )
  ).protocols(httpProtocol)
    .maxDuration(60.seconds)
    .assertions(
      // Validamos que al menos el 70% de peticiones sean exitosas o tengan fallback
      global.successfulRequests.percent.gte(70),
      // Validamos que el tiempo de respuesta del percentil 95 sea razonable
      global.responseTime.percentile(95).lt(5000)
    )
}
