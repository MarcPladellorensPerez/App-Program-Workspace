package com.mpladellorens.delivaryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RouteAdapter routeAdapter;
    private String userLoginId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        recyclerView = findViewById(R.id.RouteIds);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list at the beginning
        routeAdapter = new RouteAdapter(new ArrayList<>(), new ArrayList<>(), "string", new ArrayList<>(), R.layout.route);
        recyclerView.setAdapter(routeAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);
        String id = userLoginId;
        if (id == null) {
            Log.d("RoutesList", "userId is null");
        } else {
            Log.d("RoutesList", "userId: " + id);
        }


        firebaseFetchUtils fetchUtils = new firebaseFetchUtils();
        //agafem larray de ids de dins de la business  que necesitem dins de lusuari registrat
        Log.d("RoutesList23", "userId: " + id);

        fetchUtils.fetchBusinessdData(id,"routes", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> routeIds) {
                fetchUtils.fetchFieldData(routeIds, "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {

                    @Override
                    public void onCallback(List<route> itemList, List<String> itemIdList) {

                        CollectionReference routesRef = db.collection("Routes");

                        routesRef.whereIn(FieldPath.documentId(), routeIds).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Map<String, route> routeMap = new HashMap<>();
                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                    route routeItem = document.toObject(route.class);
                                    routeItem.setId(document.getId()); // Set the document ID
                                    routeMap.put(document.getId(), routeItem);
                                }
                                List<route> routes = new ArrayList<>();
                                for (String routeId : routeIds) {
                                    routes.add(routeMap.get(routeId));
                                }
                                for (route item : routes) {
                                    // Access the id of the route
                                    String id = item.getId();
                                    // Continue processing the route item as needed
                                }
                                RecyclerView routeIdsRecyclerView = findViewById(R.id.RouteIds);
                                routeIdsRecyclerView.setLayoutManager(new LinearLayoutManager(RouteList.this));

                                List<String> checkedIds= new ArrayList<>();
                                routeAdapter = new RouteAdapter(new ArrayList<>(), routeIds, userLoginId, checkedIds, R.layout.route);
                                routeIdsRecyclerView.setAdapter(routeAdapter);
                                routeAdapter.updateData(routes);
                            }
                        });
                    }
                });
            }
        });

        // Set OnClickListener for the CreateRouteButton
        findViewById(R.id.CreateRoute).setOnClickListener(v -> {
            // Launch the CreateRoute activity
            Intent intent = new Intent(RouteList.this, EditRoute.class);
            startActivity(intent);
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // After editing or adding an item, when you want to go back to the main menu
        Intent intent = new Intent(this, MainMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // This flag ensures that all the activities on top of the MainActivity are finished.
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Fetch the data again
        firebaseFetchUtils fetchUtils = new firebaseFetchUtils();
        fetchUtils.fetchBusinessdData(userLoginId,"routes", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> routeIds) {
                fetchUtils.fetchFieldData(routeIds, "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {

                    @Override
                    public void onCallback(List<route> itemList, List<String> itemIdList) {
                        // Update the adapter inside the callback function
                        routeAdapter.updateRouteIds(itemIdList);
                        routeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

}