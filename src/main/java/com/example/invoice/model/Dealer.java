package com.example.invoice.model;

public class Dealer {
    public Long id;
    public String name;
    public String address;
    public String phone;
    public String taxNumber;

    public Dealer() {}
    public Dealer(Long id, String name, String address, String phone, String taxNumber) {
        this.id = id; this.name = name; this.address = address; this.phone = phone; this.taxNumber = taxNumber;
    }
}
