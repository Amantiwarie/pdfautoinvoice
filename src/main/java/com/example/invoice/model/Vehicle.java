package com.example.invoice.model;

import java.math.BigDecimal;

public class Vehicle {
    public Long id;
    public String make;
    public String model;
    public String vin;
    public BigDecimal price;

    public Vehicle() {}
    public Vehicle(Long id, String make, String model, String vin, BigDecimal price) {
        this.id = id; this.make = make; this.model = model; this.vin = vin; this.price = price;
    }
}
