package com.alticelabs.sigo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Path("/validated")
public class ExampleResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response validated() throws IOException {
        try (InputStream input = getClass().getResourceAsStream("/validated-response.json")) {
            if (input == null) {
                return Response.serverError().build();
            }
            return Response.ok(new String(input.readAllBytes(), StandardCharsets.UTF_8)).build();
        }
    }
}