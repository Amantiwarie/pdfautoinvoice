package com.example.invoice.config;

import com.example.invoice.model.Dealer;
import com.example.invoice.model.Vehicle;
import com.example.invoice.repository.DealerRepository;
import com.example.invoice.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {
    
    private final DealerRepository dealerRepository;
    private final VehicleRepository vehicleRepository;
    
    public DataSeeder(DealerRepository dealerRepository, VehicleRepository vehicleRepository) {
        this.dealerRepository = dealerRepository;
        this.vehicleRepository = vehicleRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (dealerRepository.count() == 0) {
            seedDealers();
        }
        
        if (vehicleRepository.count() == 0) {
            seedVehicles();
        }
    }
    
    private void seedDealers() {
        dealerRepository.save(new Dealer(null, "Dealer Auto Center", "221B Baker St, London", "+44 20 1234 5678", "TAX-GB-123456"));
        dealerRepository.save(new Dealer(null, "City Motors", "12 Park Ave, Mumbai", "+91 22 5555 0000", "TAX-IN-654321"));
        dealerRepository.save(new Dealer(null, "Tata Motors Showroom", "Andheri East, Mumbai", "+91 22 4001 2345", "TAX-IN-112233"));
        dealerRepository.save(new Dealer(null, "Mahindra Auto World", "Baner, Pune", "+91 20 5678 1234", "TAX-IN-445566"));
        dealerRepository.save(new Dealer(null, "Maruti Suzuki Arena", "Koramangala, Bengaluru", "+91 80 2345 6789", "TAX-IN-778899"));
        dealerRepository.save(new Dealer(null, "Hyundai Prime Cars", "Gachibowli, Hyderabad", "+91 40 1234 5678", "TAX-IN-991122"));
        dealerRepository.save(new Dealer(null, "Honda City Auto", "Anna Salai, Chennai", "+91 44 8765 4321", "TAX-IN-334455"));
        dealerRepository.save(new Dealer(null, "Kia Motors Hub", "Salt Lake, Kolkata", "+91 33 7654 3210", "TAX-IN-667788"));
    }
    
    private void seedVehicles() {
        vehicleRepository.save(new Vehicle(null, "Toyota", "Corolla", "JTDBR32E930056789", new BigDecimal("12000.00")));
        vehicleRepository.save(new Vehicle(null, "Honda", "Civic", "2HGFB2F59DH012345", new BigDecimal("14000.00")));
        vehicleRepository.save(new Vehicle(null, "Tata", "Harrier", "MAT62751234567890", new BigDecimal("18000.00")));
        vehicleRepository.save(new Vehicle(null, "Mahindra", "XUV700", "MAH1234XUV7005678", new BigDecimal("22000.00")));
        vehicleRepository.save(new Vehicle(null, "Maruti Suzuki", "Swift", "MSILSWIFT098765432", new BigDecimal("9000.00")));
        vehicleRepository.save(new Vehicle(null, "Hyundai", "Creta", "HYUNCRETA123456789", new BigDecimal("17000.00")));
        vehicleRepository.save(new Vehicle(null, "Kia", "Seltos", "KIASLT12345678901", new BigDecimal("16000.00")));
        vehicleRepository.save(new Vehicle(null, "Honda", "City", "HONDACTY1234567890", new BigDecimal("15000.00")));
        vehicleRepository.save(new Vehicle(null, "Tata", "Nexon", "TATANEX12345678901", new BigDecimal("13000.00")));
        vehicleRepository.save(new Vehicle(null, "Mahindra", "Scorpio N", "MAHSCORP9876543210", new BigDecimal("21000.00")));
        vehicleRepository.save(new Vehicle(null, "Maruti Suzuki", "Baleno", "MSILBALENO12345678", new BigDecimal("11000.00")));
        vehicleRepository.save(new Vehicle(null, "Hyundai", "Venue", "HYUNVENUE123456789", new BigDecimal("14000.00")));
        vehicleRepository.save(new Vehicle(null, "Toyota", "Innova Crysta", "TOYINNOVA123456789", new BigDecimal("25000.00")));
        vehicleRepository.save(new Vehicle(null, "Kia", "Carens", "KIACARENS987654321", new BigDecimal("20000.00")));
        vehicleRepository.save(new Vehicle(null, "MG", "Hector", "MGHECTOR1234567890", new BigDecimal("23000.00")));
    }
}
