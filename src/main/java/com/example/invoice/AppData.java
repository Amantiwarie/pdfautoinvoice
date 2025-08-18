package com.example.invoice;

import com.example.invoice.model.Dealer;
import com.example.invoice.model.Vehicle;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppData {
    public final Map<Long, Dealer> dealers = new HashMap<>();
    public final Map<Long, Vehicle> vehicles = new HashMap<>();

    @PostConstruct
    public void seed() {
        dealers.put(1L, new Dealer(1L, "Dealer Auto Center", "221B Baker St, London", "+44 20 1234 5678", "TAX-GB-123456"));
        dealers.put(2L, new Dealer(2L, "City Motors", "12 Park Ave, Mumbai", "+91 22 5555 0000", "TAX-IN-654321"));
        vehicles.put(101L, new Vehicle(101L, "Toyota", "Corolla", "JTDBR32E930056789", new BigDecimal("12000.00")));
        vehicles.put(102L, new Vehicle(102L, "Honda", "Civic", "2HGFB2F59DH012345", new BigDecimal("14000.00")));
    }
}
