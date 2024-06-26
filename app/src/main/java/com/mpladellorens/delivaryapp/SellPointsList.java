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

import java.util.ArrayList;
import java.util.List;

public class SellPointsList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SellPointsAdapter SellPointsAdapter;
    String userLoginId;
    Button deleteSellPoints;
    Button ConfirmDeleteSellPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_points_list);
        ConfirmDeleteSellPoints = findViewById(R.id.ConfirmDeleteSellPoints);
        deleteSellPoints = findViewById(R.id.deleteSellPoints);
        recyclerView = findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list at the beginning
        String employeeId = ""; // Replace with your employee ID
        List<String> checkedIds = new ArrayList<>(); // Initialize with your checked IDs
        SellPointsAdapter = new SellPointsAdapter(new ArrayList<>(), new ArrayList<>(), employeeId, checkedIds, R.layout.sellpoint_template,this,recyclerView); // Replace R.layout.your_layout with your actual layout ID
        recyclerView.setAdapter(SellPointsAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);
        if (userLoginId == null) {
            Log.d("SellRoutesList", "userId is null");
        } else {
            Log.d("SellRoutesList", "userId: " + userLoginId);
        }


        firebaseFetchUtils fetchUtils = new firebaseFetchUtils();
        //agafem larray de ids de dins de la business  que necesitem dins de lusuari registrat
        fetchUtils.fetchBusinessdData(userLoginId,"salePointsIds", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> sellRouteIds) {
                fetchUtils.fetchFieldData(sellRouteIds, "SellPoints", SellPoint.class, new firebaseFetchUtils.FetchDataFieldCallback<SellPoint>() {
                    @Override
                    public void onCallback(List<SellPoint> itemList, List<String> itemIdList) {
                        // Set the IDs of the SellPoint objects
                        for (int i = 0; i < itemList.size(); i++) {
                            SellPoint sellPoint = itemList.get(i);
                            String id = itemIdList.get(i);
                            sellPoint.setId(id);
                        }
                        Log.d("sellpoints", itemList+"  " +itemIdList.toString());

                        // Update the adapter inside the callback function
                        SellPointsAdapter.updateData(itemList, itemIdList);
                        SellPointsAdapter.notifyDataSetChanged();
                        Log.d("sellpoints", itemList+"  " +itemIdList.toString());
                    }
                });
            }
        });

        // Set OnClickListener for the CreateSellRouteButton
        findViewById(R.id.CreateSellPoint).setOnClickListener(v -> {
            // Launch the CreateSellRoute activity
            Intent intent = new Intent(SellPointsList.this, editSellPoints.class);
            startActivity(intent);
        });
        deleteSellPoints.setOnClickListener(v -> {
            if (SellPointsAdapter.deleteMode){
                SellPointsAdapter.exitDeleteMode();
            }else{
                SellPointsAdapter.enterDeleteMode();
            }
                }
        );
        ConfirmDeleteSellPoints.setOnClickListener(v -> {
            // Get the checked status of each item from the singleton
            List<Boolean> itemCheckedStatus2 = CheckedItemsSingleton.getInstance().getItemCheckedStatus2();

            // Get the IDs of the checked items
            List<String> checkedIds2 = new ArrayList<>();
            for (int i = 0; i < itemCheckedStatus2.size(); i++) {
                if (itemCheckedStatus2.get(i)) {  // If the item is checked
                    checkedIds2.add(SellPointsAdapter.sellPointIds.get(i));  // Add its ID to the list
                }
            }
            if (checkedIds2.isEmpty()) {
                SellPointsAdapter.exitDeleteMode();

                return;
            }

            // Print the IDs of the checked items
            Log.d("CheckedItems", "IDs of checked items: " + checkedIds2.toString());

            // Create an instance of deleteUtils
            Context context = this;
            deleteUtils deleteUtilsInstance = new deleteUtils(context);

            // Call the deleteData method
            deleteUtilsInstance.deleteData(checkedIds2, "SellPoints", "salePointsIds", false, true, new deleteUtils.DeleteDataCallback() {
                @Override
                public void onCallback() {
                    Log.d("DeleteData", "Data deletion successful");
                }

                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("DeleteData", "Data deletion successful");

                    // After deleting the data, fetch the updated list
                    fetchUtils.fetchBusinessdData(userLoginId, "salePointsIds", new firebaseFetchUtils.FetchDataBusinessCallback() {
                        @Override
                        public void onCallback(List<String> sellRouteIds) {
                            fetchUtils.fetchFieldData(sellRouteIds, "SellPoints", SellPoint.class, new firebaseFetchUtils.FetchDataFieldCallback<SellPoint>() {
                                @Override
                                public void onCallback(List<SellPoint> itemList, List<String> itemIdList) {
                                    // Update the adapter inside the callback function
                                    SellPointsAdapter.updateData(itemList, itemIdList);
                                    SellPointsAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    SellPointsAdapter.exitDeleteMode();
                }
            });
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
        fetchUtils.fetchBusinessdData(userLoginId,"salePointsIds", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> sellRouteIds) {
                fetchUtils.fetchFieldData(sellRouteIds, "SellPoints", SellPoint.class, new firebaseFetchUtils.FetchDataFieldCallback<SellPoint>() {
                    @Override
                    public void onCallback(List<SellPoint> itemList, List<String> itemIdList) {
                        // Update the adapter inside the callback function
                        SellPointsAdapter.updateData(itemList, itemIdList);
                        SellPointsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
    private void refreshData() {
        firebaseFetchUtils fetchUtils = new firebaseFetchUtils();
        fetchUtils.fetchBusinessdData(userLoginId,"salePointsIds", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> sellRouteIds) {
                fetchUtils.fetchFieldData(sellRouteIds, "SellPoints", SellPoint.class, new firebaseFetchUtils.FetchDataFieldCallback<SellPoint>() {
                    @Override
                    public void onCallback(List<SellPoint> itemList, List<String> itemIdList) {
                        // Update the adapter inside the callback function
                        SellPointsAdapter.updateData(itemList, itemIdList);
                        SellPointsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

}