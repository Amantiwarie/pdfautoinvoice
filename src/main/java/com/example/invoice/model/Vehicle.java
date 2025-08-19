package com.example.invoice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @NotBlank
    @Column(nullable = false)
    public String make;
    
    @NotBlank
    @Column(nullable = false)
    public String model;
    
    @NotBlank
    @Column(nullable = false, unique = true)
    public String vin;
    
    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;

    public Vehicle() {}
    public Vehicle(Long id, String make, String model, String vin, BigDecimal price) {
        this.id = id; this.make = make; this.model = model; this.vin = vin; this.price = price;
    }
}
