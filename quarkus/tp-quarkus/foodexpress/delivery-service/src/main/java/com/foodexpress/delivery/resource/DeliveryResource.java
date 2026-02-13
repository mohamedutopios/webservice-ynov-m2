package com.foodexpress.delivery.resource;

import com.foodexpress.delivery.dto.DeliveryRequest;
import com.foodexpress.delivery.dto.DeliveryStatusUpdate;
import com.foodexpress.delivery.entity.Delivery;
import com.foodexpress.delivery.service.DeliveryService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api/deliveries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeliveryResource {

    @Inject
    DeliveryService deliveryService;

    @GET
    @Path("/{id}")
    public Delivery getById(@PathParam("id") Long id) {
        return deliveryService.findById(id);
    }

    @POST
    public Response create(@Valid DeliveryRequest request) {
        Delivery delivery = deliveryService.create(request);
        return Response.created(URI.create("/api/deliveries/" + delivery.id))
                .entity(delivery).build();
    }

    @PUT
    @Path("/{id}/status")
    public Delivery updateStatus(@PathParam("id") Long id, @Valid DeliveryStatusUpdate update) {
        return deliveryService.updateStatus(id, update.status);
    }

    @GET
    @Path("/driver/{driverId}")
    public List<Delivery> getByDriver(@PathParam("driverId") Long driverId) {
        return deliveryService.findByDriver(driverId);
    }

    @GET
    @Path("/order/{orderId}")
    public Delivery getByOrderId(@PathParam("orderId") Long orderId) {
        return deliveryService.findByOrderId(orderId);
    }
}
