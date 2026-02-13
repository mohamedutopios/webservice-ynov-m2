package com.foodexpress.order.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/restaurants")
@RegisterRestClient(configKey = "restaurant-api")
@Produces(MediaType.APPLICATION_JSON)
public interface RestaurantRestClient {

    @GET
    @Path("/{id}")
    RestaurantDTO getById(@PathParam("id") Long id);

    @GET
    @Path("/{id}/dishes/{dishId}")
    DishDTO getDish(@PathParam("id") Long restaurantId, @PathParam("dishId") Long dishId);

    @GET
    @Path("/{id}/dishes/{dishId}/exists")
    boolean dishExists(@PathParam("id") Long restaurantId, @PathParam("dishId") Long dishId);
}
