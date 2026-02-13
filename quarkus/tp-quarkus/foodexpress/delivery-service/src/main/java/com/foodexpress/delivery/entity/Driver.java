package com.foodexpress.delivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(name = "first_name", nullable = false)
    public String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "last_name", nullable = false)
    public String lastName;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Le numéro n'est pas valide")
    public String phone;

    @NotNull(message = "Le type de véhicule est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    public VehicleType vehicleType;

    public boolean available = true;

    @Column(name = "current_zone")
    public String currentZone;

    public Double rating = 5.0;

    public enum VehicleType {
        BIKE, SCOOTER, CAR
    }
}
