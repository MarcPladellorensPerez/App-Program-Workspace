package com.mpladellorens.delivaryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SellPointsList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SellPointsAdapter SellPointsAdapter;
    String userLoginId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_points_list);

        recyclerView = findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list at the beginning
        SellPointsAdapter = new SellPointsAdapter(new ArrayList<>(), new ArrayList<>(), SellPointsList.this);
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
                        // Update the adapter inside the callback function
                        SellPointsAdapter.updateData(itemList, itemIdList);
                        SellPointsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        // Set OnClickListener for the CreateSellRouteButton
        findViewById(R.id.CreateSellPoint).setOnClickListener(v -> {
            // Launch the CreateSellRoute activity
            Intent intent = new Intent(SellPointsList.this, createSellPoint.class);
            startActivity(intent);
        });

    }

}