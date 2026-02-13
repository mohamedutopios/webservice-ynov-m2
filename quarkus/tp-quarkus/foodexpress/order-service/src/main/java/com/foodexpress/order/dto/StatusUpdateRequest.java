package com.foodexpress.order.dto;

import com.foodexpress.order.entity.Order;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Le nouveau statut est obligatoire")
    public Order.OrderStatus status;
}
