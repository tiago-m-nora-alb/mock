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
}
