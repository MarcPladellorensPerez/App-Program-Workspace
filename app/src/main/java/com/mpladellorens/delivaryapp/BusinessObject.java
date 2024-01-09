package com.mpladellorens.delivaryapp;

public class BusinessObject {
    public String name;
    public String email;
    public String address;
    public String password;
    public String phone;

    // Constructors, getters, setters, etc. could be added based on your requirements

    public BusinessObject() {
        // Default constructor (required for Firebase)
    }

    public BusinessObject(String name, String email, String address, String password, String phone) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.password = password;
        this.phone = phone;
    }
}