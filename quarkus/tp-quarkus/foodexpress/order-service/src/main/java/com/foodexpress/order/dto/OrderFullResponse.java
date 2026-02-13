package com.foodexpress.order.dto;

import com.foodexpress.order.client.CustomerDTO;
import com.foodexpress.order.client.DeliveryDTO;
import com.foodexpress.order.client.RestaurantDTO;
import com.foodexpress.order.entity.Order;

public class OrderFullResponse {

    public Order order;
    public CustomerDTO customer;
    public RestaurantDTO restaurant;
    public DeliveryDTO delivery;

    public OrderFullResponse() {}

    public OrderFullResponse(Order order, CustomerDTO customer, RestaurantDTO restaurant, DeliveryDTO delivery) {
        this.order = order;
        this.customer = customer;
        this.restaurant = restaurant;
        this.delivery = delivery;
    }
}
