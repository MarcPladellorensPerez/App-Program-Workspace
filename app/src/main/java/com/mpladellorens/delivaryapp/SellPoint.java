package com.mpladellorens.delivaryapp;

import java.io.Serializable;

public class SellPoint implements Serializable {
    private String id; // Add this line
    private String name;
    private String description;
    private double latitude;
    private double longitude;


    public SellPoint() {}
    public void setId(String id) { this.id = id; } // Add this line

    // Getter methods
    public String getId() { return id; } // Add this line
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
