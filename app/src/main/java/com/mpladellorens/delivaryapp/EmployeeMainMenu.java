package com.mpladellorens.delivaryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.squareup.okhttp.Route;

import java.util.ArrayList;
import java.util.List;

public class EmployeeMainMenu extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RouteAdapter routeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_main_menu);

        recyclerView = findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        routeAdapter = new RouteAdapter(new ArrayList<>(), new ArrayList<>(), "string", new ArrayList<>(), R.layout.route,EmployeeMainMenu.this, recyclerView);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);
        Log.d("nasdaj", userLoginId);
        firebaseFetchUtils fetchUtils= new firebaseFetchUtils();
        fetchUtils.fetchEmployeedData(userLoginId, new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> routeIds) {
                // Now you have the routeIds
                // Use them to fetch the corresponding data
                fetchUtils.fetchFieldData(routeIds, "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {
                    @Override
                    public void onCallback(List<route> routes, List<String> routeIdList) {
                        Log.d("asdasd",routes.toString());
                        routeAdapter.updateData(routes);
                        routeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}