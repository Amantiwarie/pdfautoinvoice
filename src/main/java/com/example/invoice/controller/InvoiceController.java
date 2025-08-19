package com.example.invoice.controller;

import com.example.invoice.dto.InvoiceRequest;
import com.example.invoice.model.Dealer;
import com.example.invoice.model.Vehicle;
import com.example.invoice.repository.DealerRepository;
import com.example.invoice.repository.VehicleRepository;
import com.example.invoice.service.InvoicePdfService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;
    private final InvoicePdfService pdf;

    public InvoiceController(DealerRepository dealerRepository, VehicleRepository vehicleRepository, InvoicePdfService pdf) {
        this.dealerRepository = dealerRepository;
        this.vehicleRepository = vehicleRepository;
        this.pdf = pdf;
    }


    @PostMapping(value = "/invoices", produces = "application/pdf")
    public ResponseEntity<byte[]> create(@Valid @RequestBody InvoiceRequest req) throws Exception {
        Dealer dealer = dealerRepository.findById(req.dealerId)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));
        Vehicle vehicle = vehicleRepository.findById(req.vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        var payload = pdf.buildPayload(dealer, vehicle, req.customerName);
        byte[] bytes = pdf.renderPdf(payload);
        String filename = ("invoice-" + payload.invoiceNo + ".pdf").replaceAll("[^A-Za-z0-9._-]", "_");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
