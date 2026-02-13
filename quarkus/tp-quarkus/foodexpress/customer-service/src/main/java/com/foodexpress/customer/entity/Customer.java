package com.foodexpress.customer.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends PanacheEntity {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Column(name = "first_name", nullable = false)
    public String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Column(name = "last_name", nullable = false)
    public String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(nullable = false, unique = true)
    public String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Le numéro de téléphone n'est pas valide")
    public String phone;

    @NotBlank(message = "L'adresse est obligatoire")
    public String address;

    @NotBlank(message = "La ville est obligatoire")
    public String city;

    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit contenir 5 chiffres")
    @Column(name = "zip_code")
    public String zipCode;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    public boolean active = true;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


    public static List<Customer> findByCity(String city) {
        return list("city = ?1 and active = true", city);
    }

    public static Customer findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public static List<Customer> findAllActive() {
        return list("active", true);
    }

    public static long countByCity(String city) {
        return count("city", city);
    }
}
