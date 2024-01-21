/*
 * Copyright (C)2023-2025 Skytel MTEL.
 * All rights reserved
 *
 * SkyHealth v2.0
 */
package py.com.icarusdb.reactiveexample;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author Roberto Gamarra [roberto.gamarra@skytel.com.py]
 */
@Path("v1/countries")
@Produces(MediaType.APPLICATION_JSON)
public class CountriesResource {

    public static final String RESOURCE_URI = "v1/countries";

    @Inject
    private CountryRepository repository;

    @GET
    @Operation(description = "says Hi!.")
    @APIResponse(responseCode = "200", description = "Ok")
    public String index() {
        return "Hello !";
    }

    @GET
    @Path("/all")
    @Operation(description = "get a list of all Countries")
    @APIResponse(responseCode = "200", description = "Ok")
    public Uni<Response> all() {
        return repository.all()
                .onItem().transform(Response::ok)
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @GET
    @Path("{id}")
    @Operation(description = "get Country by id")
    @APIResponse(responseCode = "200", description = "Ok")
    @APIResponse(responseCode = "204", description = "No Contenct")
    public Uni<Response> findById(@PathParam("id") Long id) {
        return repository.findById(id)
                .onItem().ifNotNull().transform(country -> Response.ok(country).build())
                .onItem().ifNull().continueWith(Response.ok().status(NO_CONTENT)::build);
    }

    @GET
    @Path("/name/{name}")
    @Operation(description = "get a list of Countries by name")
    @APIResponse(responseCode = "200", description = "Ok")
    @APIResponse(responseCode = "204", description = "No Contenct")
    public Uni<Response> findByName(@PathParam("name") String name) {
        return repository.findAllByName(name)
                .onItem().ifNotNull().transform(this::buildResponseWithList)
                .onItem().ifNull().continueWith(Response.ok().status(NO_CONTENT)::build);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Create Country")
    @APIResponse(responseCode = "201", description = "Country Created")
    public Uni<Response> create(Country country) {
        return repository.create(country)
                .onItem().transform(entity -> buildUri(RESOURCE_URI, entity.getId()))
                .onItem().transform(Response::created)
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Update Country")
    @APIResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = Country.class)))
    @APIResponse(responseCode = "200", description = "Ok/updated")
    @APIResponse(responseCode = "204", description = "Country not found")
    @APIResponse(responseCode = "422", description = "Unprocessable Entity")
    public Uni<Response> update(Country country) {
        return repository.update(country)
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NO_CONTENT)::build);
    }

    @DELETE
    @Operation(description = "Remove Country")
    @APIResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = Country.class)))
    @APIResponse(responseCode = "200", description = "Ok/removed")
    @APIResponse(responseCode = "204", description = "Country not found")
    @APIResponse(responseCode = "422", description = "Unprocessable Entity")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return repository.delete(id)
                .onItem().ifNotNull().transform(
                        deleted -> deleted
                                ? Response.ok().status(NO_CONTENT).build()
                                : Response.ok().status(NOT_FOUND).build())
                .onItem().ifNull().continueWith(Response.ok().status(NO_CONTENT)::build);
    }

    private Response buildResponseWithList(Collection<?> collection) {
        return collection.isEmpty() ? Response.ok().status(NO_CONTENT).build()
                : Response.ok(collection).build();
    }

    private URI buildUri(String resourceUri, Serializable id) {
        return URI.create(resourceUri + "/" + id);
    }

}
