package com.example.invoice.controller;

import com.example.invoice.AppData;
import com.example.invoice.dto.InvoiceRequest;
import com.example.invoice.model.Dealer;
import com.example.invoice.model.Vehicle;
import com.example.invoice.service.InvoicePdfService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class InvoiceController {

    private final AppData data;
    private final InvoicePdfService pdf;

    public InvoiceController(AppData data, InvoicePdfService pdf) {
        this.data = data; this.pdf = pdf;
    }

    @GetMapping("/dealers")
    public ResponseEntity<?> dealers() {
        return ResponseEntity.ok(data.dealers.values());
    }

    @GetMapping("/vehicles")
    public ResponseEntity<?> vehicles() {
        return ResponseEntity.ok(data.vehicles.values());
    }

    @PostMapping(value = "/invoices", produces = "application/pdf")
    public ResponseEntity<byte[]> create(@Valid @RequestBody InvoiceRequest req) throws Exception {
        Dealer dealer = Optional.ofNullable(data.dealers.get(req.dealerId))
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found"));
        Vehicle vehicle = Optional.ofNullable(data.vehicles.get(req.vehicleId))
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
