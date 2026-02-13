package com.foodexpress.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class OrderRequest {

    @NotNull(message = "Le customerId est obligatoire")
    public Long customerId;

    @NotNull(message = "Le restaurantId est obligatoire")
    public Long restaurantId;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    public String deliveryAddress;

    @NotEmpty(message = "La commande doit contenir au moins un article")
    @Valid
    public List<OrderItemRequest> items;

    public static class OrderItemRequest {
        @NotNull(message = "Le dishId est obligatoire")
        public Long dishId;

        @Min(value = 1, message = "La quantité doit être au moins 1")
        public int quantity;
    }
}
