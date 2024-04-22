package com.mpladellorens.delivaryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditRoute extends AppCompatActivity {

    private EditText nameEditText;
    private EditText descriptionEditText2;
    private Button saveButton;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_route);

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText2 = findViewById(R.id.descriptionEditText2);
        saveButton = findViewById(R.id.saveRoute);
        recyclerView = findViewById(R.id.recyclerView);

        // Get the route ID from the intent
        String routeId = getIntent().getStringExtra("routeId");

        // Create an instance of firebaseFetchUtils
        firebaseFetchUtils fetchUtils = new firebaseFetchUtils();

        // Fetch the route data using fetchFieldData
        fetchUtils.fetchFieldData(Collections.singletonList(routeId), "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {
            @Override
            public void onCallback(List<route> itemList, List<String> itemIdList) {
                // Assuming itemList contains only one item
                route routeData = itemList.get(0);

                // Populate the EditText fields with the route data
                nameEditText.setText(routeData.getName());
                descriptionEditText2.setText(routeData.getDescription());
            }
        });

        // Create an instance of RouteAdapter with initial empty data
        List<String> userRoutes = new ArrayList<>();
        String employeeId = "";
        SellPointsAdapter adapter = new SellPointsAdapter(new ArrayList<>(), new ArrayList<>(), employeeId, userRoutes, R.layout.item);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);

        // Fetch the SellPointsIdList
        // Fetch all the sell points from the businesses collection
        fetchUtils.fetchBusinessdData(userLoginId, "salePointsIds", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> businessSellPointIds) {
                // Fetch the actual sell point data from the SellPoints collection
                fetchUtils.fetchFieldData(businessSellPointIds, "SellPoints", SellPoint.class, new firebaseFetchUtils.FetchDataFieldCallback<SellPoint>() {
                    @Override
                    public void onCallback(List<SellPoint> sellPointList, List<String> sellPointIdList) {
                        // Fetch the sell point IDs from the Routes collection
                        fetchUtils.fetchFieldData(Collections.singletonList(routeId), "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {
                            @Override
                            public void onCallback(List<route> routeList, List<String> itemIdList) {
                                // Assuming routeList contains only one item
                                route routeData = routeList.get(0);
                                List<String> routeSellPointIds = routeData.getSellPointIds();

                                // Compare the sell point IDs and check the ones that are in both lists
                                List<String> checkedIds = new ArrayList<>();
                                for (String id : sellPointIdList) {
                                    if (routeSellPointIds.contains(id)) {
                                        checkedIds.add(id);
                                    }
                                }

                                // Create an instance of SellPointsAdapter with the fetched sell points and checked IDs
                                SellPointsAdapter adapter = new SellPointsAdapter(sellPointList, sellPointIdList, employeeId, checkedIds, R.layout.item);

                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(EditRoute.this));
                            }
                        });
                    }
                });
            }
        });

        // Set an OnClickListener for the save button
        // Set an OnClickListener for the save button
        saveButton.setOnClickListener(v -> {
            // Get the updated name and description from the EditTexts
            String updatedName = nameEditText.getText().toString();
            String updatedDescription = descriptionEditText2.getText().toString();

            // Check if the name or description is empty
            if(updatedName.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(EditRoute.this, "Name and description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (adapter != null) {
                // Get the document reference
                DocumentReference routeRef = db.collection("Routes").document(routeId);

                // Get the checked sell points from the adapter
                CheckedStatusSingleton singleton = CheckedStatusSingleton.getInstance();
                List<Boolean> itemCheckedStatus = singleton.getItemCheckedStatus();

                List<String> checkedSellPoints = new ArrayList<>();
                Log.d("itemCheckedStatus EditRoute", itemCheckedStatus.toString());
                if (!itemCheckedStatus.isEmpty()) {
                    Log.d("Debug3", "Number of sell points: " + checkedSellPoints.size());

                    for (int i = 0; i < itemCheckedStatus.size(); i++) {
                        if (itemCheckedStatus.get(i)) {
                            checkedSellPoints.add(SellPointIdsSingleton.getInstance().getSellPointIds().get(i));
                        }
                    }
                }

                // Create a map of fields to update
                Map<String, Object> updates = new HashMap<>();
                updates.put("SellPointsIdList", checkedSellPoints);
                updates.put("name", updatedName);
                updates.put("description", updatedDescription);

                // Update the fields in the document
                routeRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("EditRoute", "DocumentSnapshot successfully updated!");
                            // End the EditRoute activity and go back to the RouteList activity
                            finish();
                        })
                        .addOnFailureListener(e -> Log.w("EditRoute", "Error updating document", e));
            }
        });
    }
}