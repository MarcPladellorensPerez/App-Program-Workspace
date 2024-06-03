package com.mpladellorens.delivaryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
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
    Button deleteRoutes;
    Button ConfirmDeleteRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        recyclerView = findViewById(R.id.RouteIds);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ConfirmDeleteRoutes = findViewById(R.id.ConfirmDeleteRoutes);
        deleteRoutes = findViewById(R.id.deleteRoutes);

        // Initialize the adapter with an empty list at the beginning
        routeAdapter = new RouteAdapter(new ArrayList<>(), new ArrayList<>(), "string", new ArrayList<>(), R.layout.route, RouteList.this, recyclerView);
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
        Log.d("RoutesList23", "userId: " + id);

        fetchUtils.fetchBusinessdData(id, "routes", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> routeIds) {
                if (routeIds == null || routeIds.isEmpty()) {
                    Log.d("RoutesList", "No routes found for the user.");
                    return;
                }

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

                                RecyclerView routeIdsRecyclerView = findViewById(R.id.RouteIds);
                                routeIdsRecyclerView.setLayoutManager(new LinearLayoutManager(RouteList.this));

                                List<String> checkedIds = new ArrayList<>();
                                routeAdapter = new RouteAdapter(new ArrayList<>(), routeIds, userLoginId, checkedIds, R.layout.route, RouteList.this, recyclerView);
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
            Intent intent = new Intent(RouteList.this, EditRoute.class);
            startActivity(intent);
        });

        deleteRoutes.setOnClickListener(v -> {
            if (routeAdapter.deleteMode) {
                routeAdapter.exitDeleteMode();
            } else {
                routeAdapter.enterDeleteMode();
            }
        });

        ConfirmDeleteRoutes.setOnClickListener(v -> {
            List<Boolean> checkedItems = CheckedItemsSingleton.getInstance().getItemCheckedStatus2();
            Log.d("AAAA", CheckedItemsSingleton.getInstance().getItemCheckedStatus2().toString());
            List<String> checkedIds = new ArrayList<>();
            for (int i = 0; i < checkedItems.size(); i++) {
                if (checkedItems.get(i)) {
                    checkedIds.add(routeAdapter.routeIds.get(i));
                }
            }
            if (checkedIds.isEmpty()) {
                routeAdapter.exitDeleteMode();
                return;
            }

            Log.d("CheckedItems", "IDs of checked items: " + checkedIds.toString());

            Context context = this;
            deleteUtils deleteUtilsInstance = new deleteUtils(context);

            deleteUtilsInstance.deleteData(checkedIds, "Routes", "routes", false, true, new deleteUtils.DeleteDataCallback() {
                @Override
                public void onCallback() {
                    Log.d("DeleteData", "Data deletion successful");
                }

                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("DeleteData", "Data deletion successful");

                    fetchUtils.fetchBusinessdData(userLoginId, "routes", new firebaseFetchUtils.FetchDataBusinessCallback() {
                        @Override
                        public void onCallback(List<String> routeIds) {
                            fetchUtils.fetchFieldData(routeIds, "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {
                                @Override
                                public void onCallback(List<route> itemList, List<String> itemIdList) {
                                    routeAdapter.updateData(itemList);
                                    routeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    routeAdapter.exitDeleteMode();
                }
            });
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseFetchUtils fetchUtils = new firebaseFetchUtils();
        fetchUtils.fetchBusinessdData(userLoginId, "routes", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> routeIds) {
                if (routeIds == null || routeIds.isEmpty()) {
                    Log.d("RoutesList", "No routes found for the user.");
                    return;
                }

                fetchUtils.fetchFieldData(routeIds, "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {
                    @Override
                    public void onCallback(List<route> itemList, List<String> itemIdList) {
                        routeAdapter.updateRouteIds(itemIdList);
                        routeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
