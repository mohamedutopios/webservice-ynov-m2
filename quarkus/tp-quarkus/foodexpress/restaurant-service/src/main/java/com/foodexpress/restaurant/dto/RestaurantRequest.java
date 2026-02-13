package com.foodexpress.restaurant.dto;

import jakarta.validation.constraints.*;
import java.time.LocalTime;

public class RestaurantRequest {

    @NotBlank @Size(min = 2, max = 100)
    public String name;

    @NotBlank
    public String cuisine;

    @NotBlank
    public String address;

    @NotBlank
    public String city;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    public String phone;

    public LocalTime openingTime;
    public LocalTime closingTime;
}
