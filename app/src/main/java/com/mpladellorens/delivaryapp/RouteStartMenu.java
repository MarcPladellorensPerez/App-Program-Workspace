package com.mpladellorens.delivaryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.Route;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteStartMenu extends AppCompatActivity {
    TextView nameEditText;
    TextView descriptionEditText3;
    Button startButton;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_start_menu);
        nameEditText = findViewById(R.id.RouteNameStart);
        descriptionEditText3 = findViewById(R.id.RouteDescriptionStart);
        startButton = findViewById(R.id.button);
        recyclerView = findViewById(R.id.recyclerView2);

        String routeId = getIntent().getStringExtra("routeId");
        Log.d("ASJDASJLD", getIntent().getExtras().toString());
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);
        firebaseFetchUtils fetchUtils = new firebaseFetchUtils();
        ArrayList<String> listIds = new ArrayList<>();
        listIds.add(routeId);
        fetchUtils.fetchFieldData(Collections.singletonList(routeId), "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {
            @Override
            public void onCallback(List<route> itemList, List<String> itemIdList) {
                if (!itemList.isEmpty()) {
                    route routeData = itemList.get(0);

                    // Populate the EditText fields with the route data
                    nameEditText.setText(routeData.getName());
                    descriptionEditText3.setText(routeData.getDescription());
                }
            }
        });
        fetchUtils.fetchOtherData(listIds.get(0), "Routes", "SellPointsIdList", new firebaseFetchUtils.FetchDataListCallback() {
            @Override
            public void onCallback(List<String> list) {
                // Now that we have the list, fetch the field data
                fetchUtils.fetchFieldData(list, "SellPoints", SellPoint.class, new firebaseFetchUtils.FetchDataFieldCallback<SellPoint>() {
                    @Override
                    public void onCallback(List<SellPoint> itemList, List<String> itemIdList) {
                        Log.d("itemList", itemList.toString());
                        fetchUtils.fetchOtherData(listIds.get(0), "Routes", "SellPointsIdList", new firebaseFetchUtils.FetchDataListCallback() {
                            @Override
                            public void onCallback(List<String> list) {
                                // Now that we have the list, fetch the field data
                                // Create an ArrayList to store the latitudes and longitudes
                                final ArrayList<Double> latitudes = new ArrayList<>();
                                final ArrayList<Double> longitudes = new ArrayList<>();

                                // Create an ArrayList to store LatLng objects
                                final ArrayList<LatLng> latLngs = new ArrayList<>();

                                fetchUtils.fetchFieldData(list, "SellPoints", SellPoint.class, new firebaseFetchUtils.FetchDataFieldCallback<SellPoint>() {
                                    @Override
                                    public void onCallback(List<SellPoint> itemList, List<String> itemIdList) {
                                        for (SellPoint itemList1 : itemList) {
                                            Log.d("LatLng1", "Latitude: " + itemList1.getLatitude() + ", Longitude: " + itemList1.getLongitude());
                                        }
                                        // Loop through each SellPoint and add its latitude and longitude to the ArrayList
                                        for (SellPoint sellPoint : itemList) {
                                            LatLng latLng = new LatLng(sellPoint.getLatitude(), sellPoint.getLongitude());
                                            latLngs.add(latLng);
                                        }
                                        for (LatLng latLng : latLngs) {
                                            Log.d("LatLng1", "Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude);
                                        }


                                        // Create an instance of the SellPointsAdapter
                                        SellPointsAdapter adapter = new SellPointsAdapter(itemList, itemIdList, userLoginId, null, R.layout.sell_point_start_route, RouteStartMenu.this, recyclerView);
                                        // Set the adapter for the RecyclerView
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(RouteStartMenu.this));
                                    }
                                });
                                startButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Define the starting point (La Joviat in Manresa)
                                        LatLng startLatLng = new LatLng(41.7245, 1.8255); // Replace with the actual coordinates

                                        // Print out the latitudes and longitudes
                                        for (LatLng latLng : latLngs) {
                                            Log.d("LatLng", "Latitude: " + latLng.getLatitude() + ", Longitude: " + latLng.getLongitude());
                                        }

                                        // Define a LocationListener to receive location updates
                                        LocationListener locationListener = new LocationListener() {
                                            public void onLocationChanged(Location location) {
                                                // Called when a new location is found by the network location provider
                                                double lat = location.getLatitude();
                                                double lng = location.getLongitude();
                                                Log.d("UserLocation", "Latitude: " + lat + ", Longitude: " + lng);
                                            }

                                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                            }

                                            public void onProviderEnabled(String provider) {
                                            }

                                            public void onProviderDisabled(String provider) {
                                            }
                                        };

                                        // Get the LocationManager system service
                                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                                        // Register the listener with the LocationManager to receive location updates
                                        if (ActivityCompat.checkSelfPermission(RouteStartMenu.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RouteStartMenu.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                                        StringBuilder uri = new StringBuilder("https://www.google.com/maps/dir/" + startLatLng.getLatitude() + "," + startLatLng.getLongitude());
                                        for (int i = 0; i < latLngs.size(); i++) {
                                            uri.append("/" + latLngs.get(i).getLatitude() + "," + latLngs.get(i).getLongitude());
                                        }
                                        Uri gmmIntentUri = Uri.parse(uri.toString());
                                        Log.d("url", uri.toString());
                                        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                                        // Make the Intent explicit by setting the Google Maps package
                                        mapIntent.setPackage("com.google.android.apps.maps");

                                        // Attempt to start an activity that can handle the Intent
                                        startActivity(mapIntent);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
    // Create a custom class to store latitude and longitude pairs
    public class LatLng {
        private double latitude;
        private double longitude;

        public LatLng(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }


}