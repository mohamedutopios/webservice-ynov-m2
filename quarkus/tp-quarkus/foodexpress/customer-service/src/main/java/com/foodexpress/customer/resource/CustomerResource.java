package com.foodexpress.customer.resource;

import com.foodexpress.customer.dto.CustomerRequest;
import com.foodexpress.customer.dto.PaginatedResponse;
import com.foodexpress.customer.entity.Customer;
import com.foodexpress.customer.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @GET
    public PaginatedResponse<Customer> list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return customerService.listAll(page, size);
    }

    @GET
    @Path("/{id}")
    public Customer getById(@PathParam("id") Long id) {
        return customerService.findById(id);
    }

    @GET
    @Path("/{id}/exists")
    public boolean exists(@PathParam("id") Long id) {
        return customerService.exists(id);
    }

    @GET
    @Path("/search")
    public List<Customer> searchByCity(@QueryParam("city") String city) {
        return customerService.searchByCity(city);
    }

    @POST
    public Response create(@Valid CustomerRequest request) {
        Customer customer = customerService.create(request);
        return Response.created(URI.create("/api/customers/" + customer.id))
                .entity(customer).build();
    }

    @PUT
    @Path("/{id}")
    public Customer update(@PathParam("id") Long id, @Valid CustomerRequest request) {
        return customerService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        customerService.delete(id);
        return Response.noContent().build();
    }
}
