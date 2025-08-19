package com.example.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class VehicleRequest {
    @NotBlank(message = "Make is required")
    public String make;
    
    @NotBlank(message = "Model is required")
    public String model;
    
    @NotBlank(message = "VIN is required")
    public String vin;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    public BigDecimal price;

    public VehicleRequest() {}
    
    public VehicleRequest(String make, String model, String vin, BigDecimal price) {
        this.make = make;
        this.model = model;
        this.vin = vin;
        this.price = price;
    }
}
