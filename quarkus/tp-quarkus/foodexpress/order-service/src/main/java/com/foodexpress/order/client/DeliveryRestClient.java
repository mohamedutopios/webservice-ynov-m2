package com.foodexpress.order.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/deliveries")
@RegisterRestClient(configKey = "delivery-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DeliveryRestClient {

    @POST
    DeliveryDTO create(DeliveryCreateRequest request);

    @GET
    @Path("/{id}")
    DeliveryDTO getById(@PathParam("id") Long id);
}
