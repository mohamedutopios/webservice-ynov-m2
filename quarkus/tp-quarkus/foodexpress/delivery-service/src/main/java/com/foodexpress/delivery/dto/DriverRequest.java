package com.foodexpress.delivery.dto;

import com.foodexpress.delivery.entity.Driver;
import jakarta.validation.constraints.*;

public class DriverRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    public String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    public String lastName;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    public String phone;

    @NotNull
    public Driver.VehicleType vehicleType;

    public String currentZone;
}
