package com.mpladellorens.delivaryapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import java.util.Map;
public class editSellPoints extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sell_points);

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Needed to get the map to display immediately

        // Get the SellPoint from the Intent
        Intent intent = getIntent();
        SellPoint sellPoint = (SellPoint) intent.getSerializableExtra("EXTRA_SELLPOINT");

        // Initialize your views
        Button saveButton = findViewById(R.id.SaveSellPoint);
        EditText nameEditText = findViewById(R.id.sellPointNameEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);

        // Set the views with the SellPoint data
        nameEditText.setText(sellPoint.getName());
        descriptionEditText.setText(sellPoint.getDescription());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Once the map is ready, move the camera to the user's location
        mapView.getMapAsync(googleMap -> {
            googleMap.setMyLocationEnabled(true);

            // Get the SellPoint location from the Intent
            double sellPointLatitude = sellPoint.getLatitude();
            double sellPointLongitude = sellPoint.getLongitude();
            LatLng sellPointLocation = new LatLng(sellPointLatitude, sellPointLongitude);

            // Add a marker at the SellPoint location
            googleMap.addMarker(new MarkerOptions().position(sellPointLocation).title(sellPoint.getName()));

            // Move the camera to the SellPoint location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sellPointLocation, 15));
        });

        saveButton.setOnClickListener(v -> {
            String userDocId = intent.getStringExtra("EXTRA_USER_DOC_ID");
            DocumentReference sellPointRef = db.collection("SellPoints").document(userDocId);

            // Get the updated name and description from the EditTexts
            String updatedName = nameEditText.getText().toString();
            String updatedDescription = descriptionEditText.getText().toString();

            // Create a map of fields to update
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", updatedName);
            updates.put("description", updatedDescription);

            sellPointRef.update(updates);
            Intent sellPointListIntent = new Intent(editSellPoints.this, SellPointsList.class);
            startActivity(sellPointListIntent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}