package com.mpladellorens.delivaryapp;

import java.util.ArrayList;
import java.util.List;

public class SellPointIdsSingleton {
    private static SellPointIdsSingleton instance;
    private List<String> sellPointIds;

    private SellPointIdsSingleton() {
        sellPointIds = new ArrayList<>();
    }

    public static synchronized SellPointIdsSingleton getInstance() {
        if (instance == null) {
            instance = new SellPointIdsSingleton();
        }
        return instance;
    }

    public List<String> getSellPointIds() {
        return sellPointIds;
    }

    public void setSellPointIds(List<String> sellPointIds) {
        this.sellPointIds = sellPointIds;
    }
}
