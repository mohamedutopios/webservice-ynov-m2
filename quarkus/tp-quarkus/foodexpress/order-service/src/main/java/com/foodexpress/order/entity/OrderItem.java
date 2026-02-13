package com.foodexpress.order.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "dish_id", nullable = false)
    public Long dishId;

    @Column(name = "dish_name")
    public String dishName;

    @Column(name = "unit_price", precision = 10, scale = 2)
    public BigDecimal unitPrice;

    @Min(value = 1, message = "La quantité doit être au moins 1")
    public int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    public Order order;
}
