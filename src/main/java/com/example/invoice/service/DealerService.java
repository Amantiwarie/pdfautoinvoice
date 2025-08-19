package com.example.invoice.service;

import com.example.invoice.dto.DealerRequest;
import com.example.invoice.model.Dealer;
import com.example.invoice.repository.DealerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DealerService {
    
    private final DealerRepository dealerRepository;
    
    public DealerService(DealerRepository dealerRepository) {
        this.dealerRepository = dealerRepository;
    }
    
    public List<Dealer> getAllDealers() {
        return dealerRepository.findAll();
    }
    
    public Optional<Dealer> getDealerById(Long id) {
        return dealerRepository.findById(id);
    }
    
    public Dealer createDealer(DealerRequest request) {
        if (dealerRepository.existsByTaxNumber(request.taxNumber)) {
            throw new IllegalArgumentException("Dealer with tax number " + request.taxNumber + " already exists");
        }
        
        Dealer dealer = new Dealer();
        dealer.name = request.name;
        dealer.address = request.address;
        dealer.phone = request.phone;
        dealer.taxNumber = request.taxNumber;
        
        return dealerRepository.save(dealer);
    }
    
    public Dealer updateDealer(Long id, DealerRequest request) {
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dealer not found with id: " + id));
        
        // Check if tax number is being changed and if it already exists
        if (!dealer.taxNumber.equals(request.taxNumber) && 
            dealerRepository.existsByTaxNumber(request.taxNumber)) {
            throw new IllegalArgumentException("Dealer with tax number " + request.taxNumber + " already exists");
        }
        
        dealer.name = request.name;
        dealer.address = request.address;
        dealer.phone = request.phone;
        dealer.taxNumber = request.taxNumber;
        
        return dealerRepository.save(dealer);
    }
    
    public void deleteDealer(Long id) {
        if (!dealerRepository.existsById(id)) {
            throw new IllegalArgumentException("Dealer not found with id: " + id);
        }
        dealerRepository.deleteById(id);
    }
}
