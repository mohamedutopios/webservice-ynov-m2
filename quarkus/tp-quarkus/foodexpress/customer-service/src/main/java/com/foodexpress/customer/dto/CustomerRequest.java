package com.foodexpress.customer.dto;

import jakarta.validation.constraints.*;

public class CustomerRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50)
    public String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50)
    public String lastName;

    @NotBlank @Email
    public String email;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    public String phone;

    @NotBlank
    public String address;

    @NotBlank
    public String city;

    @NotBlank
    @Pattern(regexp = "^[0-9]{5}$")
    public String zipCode;
}
