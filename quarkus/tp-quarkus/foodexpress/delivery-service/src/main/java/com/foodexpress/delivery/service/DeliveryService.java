package com.foodexpress.delivery.service;

import com.foodexpress.delivery.dto.DeliveryRequest;
import com.foodexpress.delivery.dto.DriverRequest;
import com.foodexpress.delivery.entity.Delivery;
import com.foodexpress.delivery.entity.Delivery.DeliveryStatus;
import com.foodexpress.delivery.entity.Driver;
import com.foodexpress.delivery.exception.*;
import com.foodexpress.delivery.repository.DeliveryRepository;
import com.foodexpress.delivery.repository.DriverRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class DeliveryService {

    @Inject
    DeliveryRepository deliveryRepository;

    @Inject
    DriverRepository driverRepository;

    @Inject
    DeliveryAssignmentService assignmentService;

    private static final Set<DeliveryStatus> VALID_FROM_PENDING = Set.of(DeliveryStatus.ASSIGNED, DeliveryStatus.FAILED);
    private static final Set<DeliveryStatus> VALID_FROM_ASSIGNED = Set.of(DeliveryStatus.PICKED_UP, DeliveryStatus.FAILED);
    private static final Set<DeliveryStatus> VALID_FROM_PICKED_UP = Set.of(DeliveryStatus.IN_TRANSIT, DeliveryStatus.FAILED);
    private static final Set<DeliveryStatus> VALID_FROM_IN_TRANSIT = Set.of(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED);

    public Delivery findById(Long id) {
        Delivery delivery = deliveryRepository.findById(id);
        if (delivery == null) {
            throw new ResourceNotFoundException("Livraison avec l'id " + id + " non trouvée");
        }
        return delivery;
    }

    public Delivery findByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId);
        if (delivery == null) {
            throw new ResourceNotFoundException("Livraison pour la commande " + orderId + " non trouvée");
        }
        return delivery;
    }

    @Transactional
    public Delivery create(DeliveryRequest request) {
        Delivery delivery = new Delivery();
        delivery.orderId = request.orderId;
        delivery.pickupAddress = request.pickupAddress;
        delivery.deliveryAddress = request.deliveryAddress;
        delivery.status = DeliveryStatus.PENDING;

        // Try to auto-assign a driver
        try {
            Driver driver = assignmentService.assignDriver();
            delivery.driverId = driver.id;
            delivery.status = DeliveryStatus.ASSIGNED;
            driver.available = false;
        } catch (BusinessRuleException e) {
            // No driver available, delivery stays PENDING
        }

        deliveryRepository.persist(delivery);
        return delivery;
    }

    @Transactional
    public Delivery updateStatus(Long id, DeliveryStatus newStatus) {
        Delivery delivery = findById(id);
        validateStatusTransition(delivery.status, newStatus);
        delivery.status = newStatus;

        if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.deliveredAt = LocalDateTime.now();
            // Free up the driver
            if (delivery.driverId != null) {
                Driver driver = driverRepository.findById(delivery.driverId);
                if (driver != null) {
                    driver.available = true;
                }
            }
        }

        if (newStatus == DeliveryStatus.FAILED && delivery.driverId != null) {
            Driver driver = driverRepository.findById(delivery.driverId);
            if (driver != null) {
                driver.available = true;
            }
        }

        return delivery;
    }

    public List<Delivery> findByDriver(Long driverId) {
        return deliveryRepository.findByDriverId(driverId);
    }

    // --- Driver management ---

    @Transactional
    public Driver createDriver(DriverRequest request) {
        Driver driver = new Driver();
        driver.firstName = request.firstName;
        driver.lastName = request.lastName;
        driver.phone = request.phone;
        driver.vehicleType = request.vehicleType;
        driver.currentZone = request.currentZone;
        driver.available = true;
        driverRepository.persist(driver);
        return driver;
    }

    @Transactional
    public Driver updateDriverAvailability(Long driverId, boolean available) {
        Driver driver = driverRepository.findById(driverId);
        if (driver == null) {
            throw new ResourceNotFoundException("Livreur avec l'id " + driverId + " non trouvé");
        }
        driver.available = available;
        return driver;
    }

    public List<Driver> findAvailableDrivers() {
        return driverRepository.findAvailable();
    }

    private void validateStatusTransition(DeliveryStatus current, DeliveryStatus target) {
        Set<DeliveryStatus> allowed = switch (current) {
            case PENDING -> VALID_FROM_PENDING;
            case ASSIGNED -> VALID_FROM_ASSIGNED;
            case PICKED_UP -> VALID_FROM_PICKED_UP;
            case IN_TRANSIT -> VALID_FROM_IN_TRANSIT;
            default -> Set.of();
        };
        if (!allowed.contains(target)) {
            throw new InvalidStatusTransitionException(
                    "Transition invalide : " + current + " → " + target +
                    ". Transitions autorisées depuis " + current + " : " + allowed);
        }
    }
}
