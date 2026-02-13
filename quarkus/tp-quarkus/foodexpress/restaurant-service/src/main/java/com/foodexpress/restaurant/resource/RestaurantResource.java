package com.foodexpress.restaurant.resource;

import com.foodexpress.restaurant.dto.DishRequest;
import com.foodexpress.restaurant.dto.PaginatedResponse;
import com.foodexpress.restaurant.dto.RestaurantRequest;
import com.foodexpress.restaurant.entity.Dish;
import com.foodexpress.restaurant.entity.Restaurant;
import com.foodexpress.restaurant.service.RestaurantService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api/restaurants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestaurantResource {

    @Inject
    RestaurantService restaurantService;

    @GET
    public PaginatedResponse<Restaurant> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return restaurantService.listAll(page, size);
    }

    @GET
    @Path("/{id}")
    public Restaurant getById(@PathParam("id") Long id) {
        return restaurantService.findById(id);
    }

    @GET
    @Path("/search")
    public List<Restaurant> search(
            @QueryParam("cuisine") String cuisine,
            @QueryParam("city") String city,
            @QueryParam("minRating") Double minRating) {
        return restaurantService.search(cuisine, city, minRating);
    }

    @GET
    @Path("/open")
    public List<Restaurant> findOpen() {
        return restaurantService.findOpen();
    }

    @POST
    public Response create(@Valid RestaurantRequest request) {
        Restaurant restaurant = restaurantService.create(request);
        return Response.created(URI.create("/api/restaurants/" + restaurant.id))
                .entity(restaurant).build();
    }

    @PUT
    @Path("/{id}")
    public Restaurant update(@PathParam("id") Long id, @Valid RestaurantRequest request) {
        return restaurantService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        restaurantService.delete(id);
        return Response.noContent().build();
    }

    // --- Dish endpoints ---

    @POST
    @Path("/{id}/dishes")
    public Response addDish(@PathParam("id") Long id, @Valid DishRequest request) {
        Dish dish = restaurantService.addDish(id, request);
        return Response.created(URI.create("/api/restaurants/" + id + "/dishes/" + dish.id))
                .entity(dish).build();
    }

    @DELETE
    @Path("/{id}/dishes/{dishId}")
    public Response removeDish(@PathParam("id") Long id, @PathParam("dishId") Long dishId) {
        restaurantService.removeDish(id, dishId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/dishes/{dishId}")
    public Dish getDish(@PathParam("id") Long id, @PathParam("dishId") Long dishId) {
        return restaurantService.findDish(id, dishId);
    }

    @GET
    @Path("/{id}/dishes/{dishId}/exists")
    public boolean dishExists(@PathParam("id") Long id, @PathParam("dishId") Long dishId) {
        return restaurantService.dishExists(id, dishId);
    }
}
