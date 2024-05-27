package com.mpladellorens.delivaryapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class editSellPoints extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private Button search;
    private EditText searchBar;
    private Marker marker;
    private GoogleMap googleMap;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sell_points);
        search = findViewById(R.id.search);
        searchBar = findViewById(R.id.location);
        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(), "AIzaSyCwjQINNIrQ93HMomiBa_w5J7f_C2sMH-E");
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // Needed to get the map to display immediately

        // Get the SellPoint from the Intent
        Intent intent = getIntent();
        SellPoint sellPoint = (SellPoint) intent.getSerializableExtra("EXTRA_SELLPOINT");

        // Retrieve the SellPoint ID from the Intent
        String sellPointId = intent.getStringExtra("EXTRA_USER_DOC_ID");

        // Check if it's a new SellPoint
        boolean isNewSellPoint = sellPoint == null;
        if (isNewSellPoint) {
            sellPoint = new SellPoint();
        }

        // Initialize your views
        Button saveButton = findViewById(R.id.SaveSellPoint);
        EditText nameEditText = findViewById(R.id.sellPointNameEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);

        // Set the views with the SellPoint data
        if (!isNewSellPoint) {
            nameEditText.setText(sellPoint.getName());
            descriptionEditText.setText(sellPoint.getDescription());
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Once the map is ready, move the camera to the user's location
        final List<LatLng> sellPointLocation = new ArrayList<>();
        sellPointLocation.add(new LatLng(41.7245, 1.8255)); // Manresa, Spain

        // If it's not a new SellPoint, get the location from the SellPoint
        if (!isNewSellPoint) {
            double sellPointLatitude = sellPoint.getLatitude();
            double sellPointLongitude = sellPoint.getLongitude();
            sellPointLocation.set(0, new LatLng(sellPointLatitude, sellPointLongitude));
        }

        SellPoint finalSellPoint = sellPoint;
        mapView.getMapAsync(map -> {
            this.googleMap = map;

            // Add a marker at the SellPoint location
            MarkerOptions markerOptions = new MarkerOptions().position(sellPointLocation.get(0)).title(finalSellPoint.getName());
            marker = map.addMarker(markerOptions);

            // Move the camera to the SellPoint location
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sellPointLocation.get(0), 15));

            // Set a click listener for the map
            map.setOnMapClickListener(latLng -> {
                // Update the marker position
                marker.setPosition(latLng);
                // Add the new LatLng to the sellPointLocation list
                sellPointLocation.add(latLng);
            });


            saveButton.setOnClickListener(v -> {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
                String userDocId = sharedPref.getString(getString(R.string.userId_key), null);

                // Get the updated name and description from the EditTexts
                String updatedName = nameEditText.getText().toString();
                String updatedDescription = descriptionEditText.getText().toString();

                // Create a map of fields to update
                Map<String, Object> updates = new HashMap<>();
                LatLng lastLatLng = sellPointLocation.get(sellPointLocation.size() - 1);
                updates.put("name", updatedName);
                updates.put("description", updatedDescription);
                updates.put("latitude", lastLatLng.latitude);
                updates.put("longitude", lastLatLng.longitude);

                // If it's a new SellPoint, add it to the database
                if (isNewSellPoint) {
                    db.collection("SellPoints").add(updates).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String newSellPointId = documentReference.getId();

                            // Get a reference to the 'business' document for the current user
                            DocumentReference businessRef = db.collection("businesses").document(userDocId);

                            // Add the new SellPoint ID to the 'salePointsIds' array
                            businessRef.update("salePointsIds", FieldValue.arrayUnion(newSellPointId));
                        }
                    });
                } else {
                    // Ensure sellPointId is not null before updating
                    if (sellPointId != null) {
                        DocumentReference sellPointRef = db.collection("SellPoints").document(sellPointId);
                        sellPointRef.update(updates);
                    } else {
                        // Handle the case where sellPointId is null
                        Log.d("editSellPoints", "sellPointId is null");
                    }
                }

                Intent sellPointListIntent = new Intent(editSellPoints.this, SellPointsList.class);
                startActivity(sellPointListIntent);
                finish();
            });
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString();

                // Create a new token for the autocomplete session
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                // Use the builder to create a FindAutocompletePredictionsRequest
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(query)
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        Log.i(TAG, prediction.getPlaceId());
                        Log.i(TAG, prediction.getPrimaryText(null).toString());

                        // Fetch the place details using the place ID
                        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(prediction.getPlaceId(), placeFields).build();
                        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
                            Place place = fetchPlaceResponse.getPlace();
                            LatLng latLng = place.getLatLng();
                            if (latLng != null) {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                marker.setPosition(latLng);
                                // Add the new LatLng to the sellPointLocation list
                                sellPointLocation.add(latLng);
                            }
                        }).addOnFailureListener((exception) -> {
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                            }
                        });
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });
            }
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