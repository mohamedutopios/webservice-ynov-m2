package com.foodexpress.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dishes")
public class Dish extends PanacheEntity {

    @NotBlank(message = "Le nom du plat est obligatoire")
    @Column(nullable = false)
    public String name;

    public String description;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    @Column(nullable = false)
    public BigDecimal price;

    @NotNull(message = "La catégorie est obligatoire")
    @Enumerated(EnumType.STRING)
    public DishCategory category;

    public boolean available = true;

    public String allergens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    public Restaurant restaurant;

    public enum DishCategory {
        STARTER, MAIN, DESSERT, DRINK
    }



    public static java.util.List<Dish> findByRestaurantAndCategory(Long restaurantId, DishCategory category) {
        return list("restaurant.id = ?1 and category = ?2 and available = true", restaurantId, category);
    }

    public static java.util.List<Dish> findAvailableByRestaurant(Long restaurantId) {
        return list("restaurant.id = ?1 and available = true", restaurantId);
    }
}
