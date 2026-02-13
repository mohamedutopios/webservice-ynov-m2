package com.foodexpress.customer.service;

import com.foodexpress.customer.dto.CustomerRequest;
import com.foodexpress.customer.dto.PaginatedResponse;
import com.foodexpress.customer.entity.Customer;
import com.foodexpress.customer.exception.DuplicateResourceException;
import com.foodexpress.customer.exception.ResourceNotFoundException;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CustomerService {

    public PaginatedResponse<Customer> listAll(int page, int size) {
        var query = Customer.findAll();
        long total = query.count();
        List<Customer> data = query.page(Page.of(page, size)).list();
        return new PaginatedResponse<>(data, page, size, total);
    }

    public Customer findById(Long id) {
        Customer customer = Customer.findById(id);
        if (customer == null) {
            throw new ResourceNotFoundException("Client avec l'id " + id + " non trouvé");
        }
        return customer;
    }

    public boolean exists(Long id) {
        return Customer.findById(id) != null;
    }

    @Transactional
    public Customer create(CustomerRequest request) {
        if (Customer.findByEmail(request.email) != null) {
            throw new DuplicateResourceException("Un client avec l'email " + request.email + " existe déjà");
        }
        Customer customer = new Customer();
        mapToEntity(request, customer);
        customer.persist();
        return customer;
    }

    @Transactional
    public Customer update(Long id, CustomerRequest request) {
        Customer customer = findById(id);
        Customer existing = Customer.findByEmail(request.email);
        if (existing != null && !existing.id.equals(id)) {
            throw new DuplicateResourceException("Un client avec l'email " + request.email + " existe déjà");
        }
        mapToEntity(request, customer);
        return customer;
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = findById(id);
        customer.active = false;
    }

    public List<Customer> searchByCity(String city) {
        return Customer.findByCity(city);
    }

    private void mapToEntity(CustomerRequest request, Customer customer) {
        customer.firstName = request.firstName;
        customer.lastName = request.lastName;
        customer.email = request.email;
        customer.phone = request.phone;
        customer.address = request.address;
        customer.city = request.city;
        customer.zipCode = request.zipCode;
    }
}
