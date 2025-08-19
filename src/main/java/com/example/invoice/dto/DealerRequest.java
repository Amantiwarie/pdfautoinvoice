package com.example.invoice.dto;

import jakarta.validation.constraints.NotBlank;

public class DealerRequest {
    @NotBlank(message = "Name is required")
    public String name;
    
    @NotBlank(message = "Address is required")
    public String address;
    
    @NotBlank(message = "Phone is required")
    public String phone;
    
    @NotBlank(message = "Tax number is required")
    public String taxNumber;

    public DealerRequest() {}
    
    public DealerRequest(String name, String address, String phone, String taxNumber) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.taxNumber = taxNumber;
    }
}
