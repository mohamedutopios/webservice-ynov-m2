package com.foodexpress.restaurant.service;

import com.foodexpress.restaurant.dto.DishRequest;
import com.foodexpress.restaurant.dto.PaginatedResponse;
import com.foodexpress.restaurant.dto.RestaurantRequest;
import com.foodexpress.restaurant.entity.Dish;
import com.foodexpress.restaurant.entity.Restaurant;
import com.foodexpress.restaurant.exception.ResourceNotFoundException;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class RestaurantService {

    public PaginatedResponse<Restaurant> listAll(int page, int size) {
        var query = Restaurant.findAll();
        long total = query.count();
        List<Restaurant> data = query.page(Page.of(page, size)).list();
        return new PaginatedResponse<>(data, page, size, total);
    }

    public Restaurant findById(Long id) {
        Restaurant restaurant = Restaurant.findById(id);
        if (restaurant == null) {
            throw new ResourceNotFoundException("Restaurant avec l'id " + id + " non trouvé");
        }
        return restaurant;
    }

    @Transactional
    public Restaurant create(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        mapToEntity(request, restaurant);
        restaurant.persist();
        return restaurant;
    }

    @Transactional
    public Restaurant update(Long id, RestaurantRequest request) {
        Restaurant restaurant = findById(id);
        mapToEntity(request, restaurant);
        return restaurant;
    }

    @Transactional
    public void delete(Long id) {
        Restaurant restaurant = findById(id);
        restaurant.active = false;
    }

    @Transactional
    public Dish addDish(Long restaurantId, DishRequest request) {
        Restaurant restaurant = findById(restaurantId);
        Dish dish = new Dish();
        dish.name = request.name;
        dish.description = request.description;
        dish.price = request.price;
        dish.category = request.category;
        dish.allergens = request.allergens;
        dish.available = true;
        dish.restaurant = restaurant;
        restaurant.dishes.add(dish);
        dish.persist();
        return dish;
    }

    @Transactional
    public void removeDish(Long restaurantId, Long dishId) {
        Restaurant restaurant = findById(restaurantId);
        Dish dish = Dish.findById(dishId);
        if (dish == null || !dish.restaurant.id.equals(restaurantId)) {
            throw new ResourceNotFoundException("Plat avec l'id " + dishId + " non trouvé dans le restaurant " + restaurantId);
        }
        restaurant.dishes.remove(dish);
        dish.delete();
    }

    public boolean dishExists(Long restaurantId, Long dishId) {
        Dish dish = Dish.findById(dishId);
        return dish != null && dish.restaurant.id.equals(restaurantId) && dish.available;
    }

    public Dish findDish(Long restaurantId, Long dishId) {
        Dish dish = Dish.findById(dishId);
        if (dish == null || !dish.restaurant.id.equals(restaurantId)) {
            throw new ResourceNotFoundException("Plat avec l'id " + dishId + " non trouvé dans le restaurant " + restaurantId);
        }
        return dish;
    }

    public List<Restaurant> search(String cuisine, String city, Double minRating) {
        if (cuisine != null && city != null) {
            return Restaurant.findByCuisineAndCity(cuisine, city);
        }
        if (cuisine != null) {
            return Restaurant.findByCuisine(cuisine);
        }
        if (city != null) {
            return Restaurant.findByCity(city);
        }
        if (minRating != null) {
            return Restaurant.findByMinRating(minRating);
        }
        return Restaurant.listAll();
    }

    public List<Restaurant> findOpen() {
        return Restaurant.findOpenAt(LocalTime.now());
    }

    private void mapToEntity(RestaurantRequest request, Restaurant restaurant) {
        restaurant.name = request.name;
        restaurant.cuisine = request.cuisine;
        restaurant.address = request.address;
        restaurant.city = request.city;
        restaurant.phone = request.phone;
        restaurant.openingTime = request.openingTime;
        restaurant.closingTime = request.closingTime;
    }
}
