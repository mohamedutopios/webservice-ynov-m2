package com.foodexpress.restaurant.dto;

import com.foodexpress.restaurant.entity.Dish;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class DishRequest {

    @NotBlank(message = "Le nom du plat est obligatoire")
    public String name;

    public String description;

    @NotNull @Positive
    public BigDecimal price;

    @NotNull
    public Dish.DishCategory category;

    public String allergens;
}
