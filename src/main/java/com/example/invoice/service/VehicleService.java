package com.example.invoice.service;

import com.example.invoice.dto.VehicleRequest;
import com.example.invoice.model.Vehicle;
import com.example.invoice.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
    
    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }
    
    public Vehicle createVehicle(VehicleRequest request) {
        if (vehicleRepository.existsByVin(request.vin)) {
            throw new IllegalArgumentException("Vehicle with VIN " + request.vin + " already exists");
        }
        
        Vehicle vehicle = new Vehicle();
        vehicle.make = request.make;
        vehicle.model = request.model;
        vehicle.vin = request.vin;
        vehicle.price = request.price;
        
        return vehicleRepository.save(vehicle);
    }
    
    public Vehicle updateVehicle(Long id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + id));
        
        // Check if VIN is being changed and if it already exists
        if (!vehicle.vin.equals(request.vin) && 
            vehicleRepository.existsByVin(request.vin)) {
            throw new IllegalArgumentException("Vehicle with VIN " + request.vin + " already exists");
        }
        
        vehicle.make = request.make;
        vehicle.model = request.model;
        vehicle.vin = request.vin;
        vehicle.price = request.price;
        
        return vehicleRepository.save(vehicle);
    }
    
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}
