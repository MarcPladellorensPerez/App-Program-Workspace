package com.mpladellorens.delivaryapp;

import java.util.List;

public class route {
    private String name;
    private String description;
    private List<String> SellPointsIdList;
    private String id;


    public route() {
        // Default constructor required for calls to DataSnapshot.getValue(RouteObject.class)
    }

    public route(String name, String description, List<String> sellPointIds) {
        this.name = name;
        this.description = description;
        this.SellPointsIdList = SellPointsIdList;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getSellPointsIdList() {
        return SellPointsIdList;
    }

    public List<String> getSellPointIds() {
        return SellPointsIdList;
    }
}