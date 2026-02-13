package com.foodexpress.order.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull(message = "Le customerId est obligatoire")
    @Column(name = "customer_id", nullable = false)
    public Long customerId;

    @NotNull(message = "Le restaurantId est obligatoire")
    @Column(name = "restaurant_id", nullable = false)
    public Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrderStatus status = OrderStatus.CREATED;

    @Column(name = "total_amount", precision = 10, scale = 2)
    public BigDecimal totalAmount;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    @Column(name = "delivery_address", nullable = false)
    public String deliveryAddress;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @NotEmpty(message = "La commande doit contenir au moins un article")
    public List<OrderItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum OrderStatus {
        CREATED, CONFIRMED, PREPARING, READY, PICKED_UP, DELIVERED, CANCELLED
    }
}
