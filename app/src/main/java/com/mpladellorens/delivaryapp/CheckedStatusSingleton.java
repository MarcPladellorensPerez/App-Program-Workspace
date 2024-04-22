package com.mpladellorens.delivaryapp;

import java.util.ArrayList;
import java.util.List;

public class CheckedStatusSingleton {
    private static CheckedStatusSingleton instance;
    private List<Boolean> itemCheckedStatus;

    private CheckedStatusSingleton() {
        itemCheckedStatus = new ArrayList<>();
    }

    public static synchronized CheckedStatusSingleton getInstance() {
        if (instance == null) {
            instance = new CheckedStatusSingleton();
        }
        return instance;
    }

    public List<Boolean> getItemCheckedStatus() {
        return itemCheckedStatus;
    }

    public void setItemCheckedStatus(List<Boolean> itemCheckedStatus) {
        this.itemCheckedStatus = itemCheckedStatus;
    }
}