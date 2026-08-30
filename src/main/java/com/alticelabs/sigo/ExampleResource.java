package com.alticelabs.sigo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ExampleResource {

    private static final String CONFIG_FILE_ENV = "MOCK_CONFIG_FILE";
    private static final String PACKAGED_CONFIG_FILE = "/mock-endpoints.json";

    @Inject
    ObjectMapper objectMapper;

    @GET
    @Path("{path: .*}")
    public Response get(@PathParam("path") String path) throws IOException {
        return configuredResponse("GET", path);
    }

    @POST
    @Path("{path: .*}")
    public Response post(@PathParam("path") String path) throws IOException {
        return configuredResponse("POST", path);
    }

    @PUT
    @Path("{path: .*}")
    public Response put(@PathParam("path") String path) throws IOException {
        return configuredResponse("PUT", path);
    }

    @PATCH
    @Path("{path: .*}")
    public Response patch(@PathParam("path") String path) throws IOException {
        return configuredResponse("PATCH", path);
    }

    @DELETE
    @Path("{path: .*}")
    public Response delete(@PathParam("path") String path) throws IOException {
        return configuredResponse("DELETE", path);
    }

    private Response configuredResponse(String method, String requestedPath) throws IOException {
        try (InputStream input = configStream()) {
            if (input == null) {
                return Response.serverError()
                        .entity(objectMapper.createObjectNode().put("error", "Mock configuration was not found"))
                        .build();
            }

            JsonNode endpoints = objectMapper.readTree(input).path("endpoints");
            if (!endpoints.isArray()) {
                return Response.serverError()
                        .entity(objectMapper.createObjectNode().put("error", "Mock configuration must contain an endpoints array"))
                        .build();
            }

            String normalizedPath = normalizePath(requestedPath);
            for (JsonNode endpoint : endpoints) {
                if (!endpoint.path("method").isTextual()
                        || !endpoint.path("path").isTextual()
                        || !endpoint.path("status").canConvertToInt()
                        || !endpoint.has("response")) {
                    return Response.serverError()
                            .entity(objectMapper.createObjectNode().put(
                                    "error",
                                    "Every endpoint must define method, path, status, and response"))
                            .build();
                }

                String configuredMethod = endpoint.path("method").asText().trim().toUpperCase(Locale.ROOT);
                String configuredPath = normalizePath(endpoint.path("path").asText());
                if (method.equals(configuredMethod) && normalizedPath.equals(configuredPath)) {
                    int status = endpoint.path("status").asInt();
                    if (status < 100 || status > 599) {
                        return Response.serverError()
                                .entity(objectMapper.createObjectNode().put("error", "Configured HTTP status must be between 100 and 599"))
                                .build();
                    }

                    Response.ResponseBuilder response = Response.status(status);
                    JsonNode body = endpoint.get("response");
                    if (body != null && !body.isNull()) {
                        response.entity(body);
                    }
                    return response.build();
                }
            }
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity(objectMapper.createObjectNode().put("error", "No mock configured for " + method + " " + normalizePath(requestedPath)))
                .build();
    }

    private InputStream configStream() throws IOException {
        String externalConfigFile = System.getenv(CONFIG_FILE_ENV);
        if (externalConfigFile != null && !externalConfigFile.isBlank()) {
            java.nio.file.Path configFile = java.nio.file.Path.of(externalConfigFile).toAbsolutePath().normalize();
            return Files.isRegularFile(configFile) ? Files.newInputStream(configFile) : null;
        }

        return getClass().getResourceAsStream(PACKAGED_CONFIG_FILE);
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank() || "/".equals(path)) {
            return "/";
        }
        return "/" + path.replaceAll("^/+|/+$", "");
    }
}
