package com.foodexpress.delivery.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    public Long orderId;

    @Column(name = "driver_id")
    public Long driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public DeliveryStatus status = DeliveryStatus.PENDING;

    @Column(name = "pickup_address")
    public String pickupAddress;

    @Column(name = "delivery_address")
    public String deliveryAddress;

    @Column(name = "estimated_time")
    public LocalDateTime estimatedTime;

    @Column(name = "delivered_at")
    public LocalDateTime deliveredAt;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.estimatedTime = LocalDateTime.now().plusMinutes(45);
    }

    public enum DeliveryStatus {
        PENDING, ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED
    }
}
