package com.example.invoice.repository;

import com.example.invoice.model.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {
    Optional<Dealer> findByTaxNumber(String taxNumber);
    boolean existsByTaxNumber(String taxNumber);
}
