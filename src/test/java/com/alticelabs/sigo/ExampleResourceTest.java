package com.alticelabs.sigo;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ExampleResourceTest {
    @Test
    void testUnknownEndpoint() {
        given().when().get("/unknown").then().statusCode(404);
    }

    @Test
    void testValidatedEndpoint() {
        given()
                .when().get("/validated")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("versionId", is(565))
                .body("values.valida1", is("valor01"))
                .body("config.size()", is(5));
    }

    @Test
    void testConfiguredMethodPathStatusAndResponse() {
        given()
                .when().post("/tickets")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("id", is(123))
                .body("created", is(true));
    }

    @Test
    void testMethodIsPartOfEndpointConfiguration() {
        given().when().get("/tickets").then().statusCode(404);
    }

    @Test
    void testConfigured400Response() {
        given()
                .when().get("/errors/400")
                .then()
                .statusCode(400)
                .body("error", is("Bad Request"))
                .body("message", is("The request is invalid"));
    }

    @Test
    void testConfigured500Response() {
        given()
                .when().get("/errors/500")
                .then()
                .statusCode(500)
                .body("error", is("Internal Server Error"))
                .body("message", is("An unexpected error occurred"));
    }
}
