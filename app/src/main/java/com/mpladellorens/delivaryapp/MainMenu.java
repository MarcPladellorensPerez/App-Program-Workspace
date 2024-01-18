package com.mpladellorens.delivaryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    Button employeesButton;
    Button restaurantButton;
    Button productButton;
    Button routeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
         employeesButton = findViewById(R.id.EmployeesButton);
         restaurantButton = findViewById(R.id.RestaurantButton);
         productButton = findViewById(R.id.ProductButton);
         routeButton = findViewById(R.id.RouteButton);
        employeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start EmployeesList activity
                Intent intent = new Intent(MainMenu.this, EmployeesList.class);
                startActivity(intent);
            }
        });

    }


}