package com.mpladellorens.delivaryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends AppCompatActivity {

    Button employeesButton;
    Button restaurantButton;
    Button sellPointsButton;
    Button routeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Context context = MainMenu.this;

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);

        employeesButton = findViewById(R.id.EmployeesButton);

         sellPointsButton = findViewById(R.id.sellPointsButton);
         routeButton = findViewById(R.id.RouteButton);
        employeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start EmployeesList activity
                Intent intent = new Intent(MainMenu.this, EmployeesList.class);
                startActivity(intent);
            }
        });
        sellPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start EmployeesList activity
                Intent intent = new Intent(MainMenu.this, SellPointsList.class);
                startActivity(intent);
            }
        });
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start EmployeesList activity
                Intent intent = new Intent(MainMenu.this, RouteList.class);
                startActivity(intent);
            }
        });
    }
}