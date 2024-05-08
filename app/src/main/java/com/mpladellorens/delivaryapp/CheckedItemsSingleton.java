package com.mpladellorens.delivaryapp;

import java.util.ArrayList;
import java.util.List;

public class CheckedItemsSingleton {
    private static CheckedItemsSingleton instance;
    public List<Boolean> itemCheckedStatus2;

    private CheckedItemsSingleton() {
        itemCheckedStatus2 = new ArrayList<>();
    }

    public static synchronized CheckedItemsSingleton getInstance() {
        if (instance == null) {
            instance = new CheckedItemsSingleton();
        }
        return instance;
    }

    public List<Boolean> getItemCheckedStatus2() {
        return itemCheckedStatus2;
    }

    public void setItemCheckedStatus2(List<Boolean> itemCheckedStatus2) {
        this.itemCheckedStatus2 = itemCheckedStatus2;
    }
}
