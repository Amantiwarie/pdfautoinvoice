package com.example.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InvoiceRequest {
    @NotNull
    public Long dealerId;
    @NotNull
    public Long vehicleId;
    @NotBlank
    public String customerName;

    public InvoiceRequest() {}
}
