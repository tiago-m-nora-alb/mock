package com.alticelabs.sigo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Path("/")
public class ExampleResource {

    private static final String RESPONSES_DIRECTORY = "MOCK_RESPONSES_DIR";

    @GET
    @Path("{endpoint}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response responseGET(@PathParam("endpoint") String endpoint) throws IOException {
        if (!endpoint.matches("[A-Za-z0-9][A-Za-z0-9._-]*")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try (InputStream input = responseStream(endpoint)) {
            if (input == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(new String(input.readAllBytes(), StandardCharsets.UTF_8)).build();
        }
    }

    @POST
    @Path("{endpoint}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response responsePOST(@PathParam("endpoint") String endpoint) throws IOException {
        if (!endpoint.matches("[A-Za-z0-9][A-Za-z0-9._-]*")) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try (InputStream input = responseStream(endpoint)) {
            if (input == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(new String(input.readAllBytes(), StandardCharsets.UTF_8)).build();
        }
    }

    private InputStream responseStream(String endpoint) throws IOException {
        String externalDirectory = System.getenv(RESPONSES_DIRECTORY);
        if (externalDirectory != null && !externalDirectory.isBlank()) {
            java.nio.file.Path directory = java.nio.file.Path.of(externalDirectory).toAbsolutePath().normalize();
            java.nio.file.Path responseFile = directory.resolve(endpoint + ".json").normalize();
            if (responseFile.startsWith(directory)
                    && Files.isRegularFile(responseFile)) {
                return Files.newInputStream(responseFile);
            }
            return null;
        }

        return getClass().getResourceAsStream("/" + endpoint + ".json");
    }
}
