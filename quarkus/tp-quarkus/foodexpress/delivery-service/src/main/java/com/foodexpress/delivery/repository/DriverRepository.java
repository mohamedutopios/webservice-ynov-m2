package com.foodexpress.delivery.repository;

import com.foodexpress.delivery.entity.Driver;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DriverRepository implements PanacheRepository<Driver> {

    public List<Driver> findAvailable() {
        return list("available", true);
    }

    public List<Driver> findAvailableByZone(String zone) {
        return list("available = true and currentZone = ?1", zone);
    }
}
