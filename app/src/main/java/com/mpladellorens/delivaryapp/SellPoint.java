package com.mpladellorens.delivaryapp;

import java.io.Serializable;

public class SellPoint implements Serializable {
    public SellPoint() {}
    private String name;
    private String description;
    private double latitude;
    private double longitude;


    // Getter methods
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getLatitude() {return latitude;
    }
    public double getLongitude() {return longitude;
    }



}

