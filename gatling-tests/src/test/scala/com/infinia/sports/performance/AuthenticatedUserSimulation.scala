package com.infinia.sports.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class AuthenticatedUserSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling/3.9.5")

  // Feeder para las credenciales de usuario
  val userFeeder = csv("users.csv").random

  // Escenario que simula a un usuario autenticado que compra
  val scn = scenario("Authenticated User Journey")
    // 1. Cargar el catálogo y guardar el cuerpo de la respuesta como texto
    .exec(http("Load Product Catalog")
      .get("/api/products")
      .check(status.is(200))
      .check(bodyString.saveAs("productsJsonString")))
    .pause(1.second)

    // 2. Alimentar la sesión con credenciales de un usuario
    .feed(userFeeder)

    // 3. Autenticar al usuario y extraer el token del cuerpo de la respuesta
    .exec(http("User Login")
      .post("/api/auth/login")
      .body(StringBody("""{"username": "${username}", "password": "${password}"}"""))
      .asJson
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("authToken")))
    .pause(1.second)

    // 4. Parsear el JSON de productos, seleccionar uno al azar y prepararlo para el carrito
    .exec(session => {
      val jsonString = session("productsJsonString").as[String]
      val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
      val products = mapper.readValue(jsonString, classOf[List[Map[String, Any]]])
      val randomProduct = products(Random.nextInt(products.size))

      // Mapeo completo y robusto basado en CartItemDTO.java
      session.set("productId", randomProduct("id"))
             .set("productName", randomProduct("description")) // El DTO tiene productName y description
             .set("description", randomProduct("description"))
             .set("unitPrice", randomProduct("price"))
             .set("productImageUrl", randomProduct("imageUrl"))
             .set("size", randomProduct.getOrElse("size", "N/A")) // Atributo de ejemplo
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
        "attributes": {
          "size": "${size}"
        }
      }"""))
      .asJson
      .check(status.is(200)))
    .pause(1.second)

    // 5. Consultar el carrito
    .exec(http("View Cart")
      .get("/api/cart")
      .header("Authorization", "Bearer ${authToken}")
      .check(status.is(200)))

  // Configuración de la carga
  setUp(
    scn.inject(rampUsers(5).during(10.seconds))
  ).protocols(httpProtocol)
}
