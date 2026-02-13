package com.foodexpress.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant extends PanacheEntity {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    public String name;

    @NotBlank(message = "Le type de cuisine est obligatoire")
    public String cuisine;

    @NotBlank
    public String address;

    @NotBlank
    public String city;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Le numéro n'est pas valide")
    public String phone;

    public Double rating = 0.0;

    @Column(name = "opening_time")
    public LocalTime openingTime;

    @Column(name = "closing_time")
    public LocalTime closingTime;

    public boolean active = true;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    public List<Dish> dishes = new ArrayList<>();

    // --- Panache Active Record queries ---

    public static List<Restaurant> findByCuisine(String cuisine) {
        return list("cuisine = ?1 and active = true", cuisine);
    }

    public static List<Restaurant> findByCity(String city) {
        return list("city = ?1 and active = true", city);
    }

    public static List<Restaurant> findByCuisineAndCity(String cuisine, String city) {
        return list("cuisine = ?1 and city = ?2 and active = true", cuisine, city);
    }

    public static List<Restaurant> findOpenAt(LocalTime time) {
        return list("openingTime <= ?1 and closingTime >= ?1 and active = true", time);
    }

    public static List<Restaurant> findByMinRating(Double rating) {
        return list("rating >= ?1 and active = true", rating);
    }
}
