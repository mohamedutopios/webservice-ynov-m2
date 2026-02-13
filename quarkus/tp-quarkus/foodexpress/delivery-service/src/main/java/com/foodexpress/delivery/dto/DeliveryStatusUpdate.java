package com.foodexpress.delivery.dto;

import com.foodexpress.delivery.entity.Delivery;
import jakarta.validation.constraints.NotNull;

public class DeliveryStatusUpdate {

    @NotNull(message = "Le statut est obligatoire")
    public Delivery.DeliveryStatus status;
}
