package com.infinia.sports.performance

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class FullLoadSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  // --- DATA FEEDERS ---
  val userFeeder = csv("users.csv").random
  val searchFeeder = csv("search_terms.csv").random

  // --- SCENARIO: ANONYMOUS USER ---
  val anonymousUserScenario = scenario("Anonymous User Journey")
    .exec(http("Load Product Catalog")
      .get("/api/products")
      .check(status.is(200)))
    .pause(1.second, 5.seconds)
    .feed(searchFeeder)
    .exec(http("Search Products")
      .get("/api/products")
      .queryParam("description", "${searchTerm}")
      .check(status.is(200)))
    .pause(1.second, 5.seconds)

  // --- SCENARIO: AUTHENTICATED USER ---
  val authenticatedUserScenario = scenario("Authenticated User Journey")
    .feed(userFeeder)
    .exec(http("User Login")
      .post("/api/auth/login")
      .body(StringBody("""{"username": "${username}", "password": "${password}"}"""))
      .asJson
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("authToken")))
    .pause(1.second)
    .exec(http("Load Product Catalog for Selection")
      .get("/api/products")
      .check(status.is(200))
      .check(bodyString.saveAs("productsJsonString")))
    .pause(1.second)
    .exec(session => {
      val jsonString = session("productsJsonString").as[String]
      val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
      val products = mapper.readValue(jsonString, classOf[List[Map[String, Any]]])
      val randomProduct = products(Random.nextInt(products.size))

      session.set("productId", randomProduct("id"))
             .set("productName", randomProduct("description"))
             .set("description", randomProduct("description"))
             .set("unitPrice", randomProduct("price"))
             .set("productImageUrl", randomProduct("imageUrl"))
             .set("size", randomProduct.getOrElse("size", "N/A"))
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
    .exec(http("View Cart")
      .get("/api/cart")
      .header("Authorization", "Bearer ${authToken}")
      .check(status.is(200)))

  // --- LOAD SETUP ---
  setUp(
    anonymousUserScenario.inject(rampUsersPerSec(1) to 8 during (10.seconds)),
    authenticatedUserScenario.inject(rampUsersPerSec(1) to 2 during (10.seconds))
  ).protocols(httpProtocol)
}
