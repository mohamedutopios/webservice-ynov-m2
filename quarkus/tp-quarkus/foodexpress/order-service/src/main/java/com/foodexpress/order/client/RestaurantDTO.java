package com.foodexpress.order.client;

import java.time.LocalTime;

public class RestaurantDTO {
    public Long id;
    public String name;
    public String cuisine;
    public String address;
    public String city;
    public LocalTime openingTime;
    public LocalTime closingTime;
    public boolean active;
}
