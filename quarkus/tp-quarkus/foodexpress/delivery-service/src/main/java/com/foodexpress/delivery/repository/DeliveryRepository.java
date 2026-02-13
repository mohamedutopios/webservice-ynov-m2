package com.foodexpress.delivery.repository;

import com.foodexpress.delivery.entity.Delivery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DeliveryRepository implements PanacheRepository<Delivery> {

    public Delivery findByOrderId(Long orderId) {
        return find("orderId", orderId).firstResult();
    }

    public List<Delivery> findByDriverId(Long driverId) {
        return list("driverId", driverId);
    }

    public List<Delivery> findActiveByDriver(Long driverId) {
        return list("driverId = ?1 and status not in ('DELIVERED', 'FAILED')", driverId);
    }

    public long countDeliveredByDriver(Long driverId) {
        return count("driverId = ?1 and status = 'DELIVERED'", driverId);
    }
}
