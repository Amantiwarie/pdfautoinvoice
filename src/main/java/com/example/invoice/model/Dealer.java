package com.example.invoice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "dealers")
public class Dealer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @NotBlank
    @Column(nullable = false)
    public String name;
    
    @NotBlank
    @Column(nullable = false)
    public String address;
    
    @NotBlank
    @Column(nullable = false)
    public String phone;
    
    @NotBlank
    @Column(nullable = false, unique = true)
    public String taxNumber;

    public Dealer() {}
    public Dealer(Long id, String name, String address, String phone, String taxNumber) {
        this.id = id; this.name = name; this.address = address; this.phone = phone; this.taxNumber = taxNumber;
    }
}
