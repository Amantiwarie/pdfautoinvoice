package com.example.invoice.controller;

import com.example.invoice.dto.DealerRequest;
import com.example.invoice.model.Dealer;
import com.example.invoice.service.DealerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealers")
public class DealerController {
    
    private final DealerService dealerService;
    
    public DealerController(DealerService dealerService) {
        this.dealerService = dealerService;
    }
    
    @GetMapping
    public ResponseEntity<List<Dealer>> getAllDealers() {
        List<Dealer> dealers = dealerService.getAllDealers();
        return ResponseEntity.ok(dealers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Dealer> getDealerById(@PathVariable Long id) {
        return dealerService.getDealerById(id)
                .map(dealer -> ResponseEntity.ok(dealer))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Dealer> createDealer(@Valid @RequestBody DealerRequest request) {
        try {
            Dealer dealer = dealerService.createDealer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(dealer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Dealer> updateDealer(@PathVariable Long id, @Valid @RequestBody DealerRequest request) {
        try {
            Dealer dealer = dealerService.updateDealer(id, request);
            return ResponseEntity.ok(dealer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealer(@PathVariable Long id) {
        try {
            dealerService.deleteDealer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
