package com.foodexpress.order.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/customers")
@RegisterRestClient(configKey = "customer-api")
@Produces(MediaType.APPLICATION_JSON)
public interface CustomerRestClient {

    @GET
    @Path("/{id}/exists")
    boolean exists(@PathParam("id") Long id);

    @GET
    @Path("/{id}")
    CustomerDTO getById(@PathParam("id") Long id);
}
