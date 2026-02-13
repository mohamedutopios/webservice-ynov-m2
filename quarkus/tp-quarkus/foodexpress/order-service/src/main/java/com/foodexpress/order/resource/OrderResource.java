package com.foodexpress.order.resource;

import com.foodexpress.order.dto.*;
import com.foodexpress.order.entity.Order;
import com.foodexpress.order.service.OrderService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;

    @GET
    public PaginatedResponse<Order> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return orderService.listAll(page, size);
    }

    @GET
    @Path("/{id}")
    public Order getById(@PathParam("id") Long id) {
        return orderService.findById(id);
    }

    @GET
    @Path("/{id}/full")
    public OrderFullResponse getFullOrder(@PathParam("id") Long id) {
        return orderService.getFullOrder(id);
    }

    @GET
    @Path("/customer/{customerId}")
    public List<Order> getByCustomer(@PathParam("customerId") Long customerId) {
        return orderService.findByCustomer(customerId);
    }

    @POST
    public Response create(@Valid OrderRequest request) {
        Order order = orderService.create(request);
        return Response.created(URI.create("/api/orders/" + order.id))
                .entity(order).build();
    }

    @PUT
    @Path("/{id}/status")
    public Order updateStatus(@PathParam("id") Long id, @Valid StatusUpdateRequest request) {
        return orderService.updateStatus(id, request.status);
    }

    @DELETE
    @Path("/{id}")
    public Response cancel(@PathParam("id") Long id) {
        orderService.cancel(id);
        return Response.noContent().build();
    }
}
