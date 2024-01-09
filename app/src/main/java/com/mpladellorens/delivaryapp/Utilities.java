package com.mpladellorens.delivaryapp;

import android.content.Context;
import android.content.Intent;

public class Utilities {
    public static void goTo(Context context, Class<?> destinationClass) {
        Intent intent = new Intent(context, destinationClass);
        context.startActivity(intent);
    }
}
