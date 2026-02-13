package com.foodexpress.delivery.resource;

import com.foodexpress.delivery.dto.DriverRequest;
import com.foodexpress.delivery.entity.Driver;
import com.foodexpress.delivery.service.DeliveryService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api/drivers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DriverResource {

    @Inject
    DeliveryService deliveryService;

    @POST
    public Response create(@Valid DriverRequest request) {
        Driver driver = deliveryService.createDriver(request);
        return Response.created(URI.create("/api/drivers/" + driver.id))
                .entity(driver).build();
    }

    @PUT
    @Path("/{id}/availability")
    public Driver updateAvailability(@PathParam("id") Long id, @QueryParam("available") boolean available) {
        return deliveryService.updateDriverAvailability(id, available);
    }

    @GET
    @Path("/available")
    public List<Driver> getAvailable() {
        return deliveryService.findAvailableDrivers();
    }
}
