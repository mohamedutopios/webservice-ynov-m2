package com.foodexpress.delivery.service;

import com.foodexpress.delivery.entity.Driver;
import com.foodexpress.delivery.exception.BusinessRuleException;
import com.foodexpress.delivery.repository.DriverRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class DeliveryAssignmentService {

    @Inject
    DriverRepository driverRepository;

    /**
     * Selects an available driver. Prioritizes drivers by rating (highest first).
     * @return the assigned Driver
     * @throws BusinessRuleException if no drivers are available
     */
    public Driver assignDriver() {
        List<Driver> available = driverRepository.findAvailable();
        if (available.isEmpty()) {
            throw new BusinessRuleException("Aucun livreur disponible actuellement");
        }
        // Pick the driver with the highest rating
        return available.stream()
                .sorted((a, b) -> Double.compare(b.rating, a.rating))
                .findFirst()
                .orElseThrow();
    }

    public Driver assignDriverInZone(String zone) {
        List<Driver> available = driverRepository.findAvailableByZone(zone);
        if (available.isEmpty()) {
            // Fallback to any available driver
            return assignDriver();
        }
        return available.stream()
                .sorted((a, b) -> Double.compare(b.rating, a.rating))
                .findFirst()
                .orElseThrow();
    }
}
