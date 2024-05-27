package com.mpladellorens.delivaryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

        routeAdapter = new RouteAdapter(new ArrayList<>(), new ArrayList<>(), "string", new ArrayList<>(), R.layout.route_main_menu_template,EmployeeMainMenu.this, recyclerView);
        recyclerView.setAdapter(routeAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);
        Log.d("nasdaj", userLoginId);

        firebaseFetchUtils fetchUtils= new firebaseFetchUtils();
        fetchUtils.fetchEmployeedData(userLoginId, new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> routeIds) {

                fetchUtils.fetchFieldData(routeIds, "Routes", route.class, new firebaseFetchUtils.FetchDataFieldCallback<route>() {
                    @Override
                    public void onCallback(List<route> routes, List<String> routeIdList) {
                        Log.d("asdasd",routes.toString());
                        recyclerView.setLayoutManager(new LinearLayoutManager(EmployeeMainMenu.this));
                        routeAdapter = new RouteAdapter(routes,routeIdList,userLoginId,new ArrayList<>(),R.layout.route_main_menu_template,EmployeeMainMenu.this,recyclerView);
                        recyclerView.setAdapter(routeAdapter);

                        routeAdapter.updateData(routes);
                        routeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // After editing or adding an item, when you want to go back to the main menu
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // This flag ensures that all the activities on top of the MainActivity are finished.
        startActivity(intent);
    }
}