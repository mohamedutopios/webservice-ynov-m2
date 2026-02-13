package com.foodexpress.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeliveryRequest {

    @NotNull(message = "L'orderId est obligatoire")
    public Long orderId;

    @NotBlank(message = "L'adresse de retrait est obligatoire")
    public String pickupAddress;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    public String deliveryAddress;
}
