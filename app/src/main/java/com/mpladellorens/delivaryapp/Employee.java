package com.mpladellorens.delivaryapp;

public class Employee {
    private String name;
    private String surname;
    private String phone;
    private String email;
    private boolean isAdmin;

    // Required empty constructor for Firestore
    public Employee() {
    }

    public Employee(String name, String surname, String phone, String email, boolean isAdmin) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}