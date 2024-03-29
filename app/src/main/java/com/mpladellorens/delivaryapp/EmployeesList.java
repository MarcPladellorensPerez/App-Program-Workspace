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

public class EmployeesList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    String userLoginId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_list);

        recyclerView = findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list at the beginning
        employeeAdapter = new EmployeeAdapter(new ArrayList<>(), new ArrayList<>(), EmployeesList.this);
        recyclerView.setAdapter(employeeAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);
        if (userLoginId == null) {
            Log.d("EmployeesList", "userId is null");
        } else {
            Log.d("EmployeesList", "userId: " + userLoginId);
        }


        firebaseFetchUtils fetchUtils = new firebaseFetchUtils();
        //agafem larray de ids de dins de la business  que necesitem dins de lusuari registrat
        fetchUtils.fetchBusinessdData(userLoginId,"EmployeesIds", new firebaseFetchUtils.FetchDataBusinessCallback() {
            @Override
            public void onCallback(List<String> employeeIds) {
                fetchUtils.fetchFieldData(employeeIds, "Employees", Employee.class, new firebaseFetchUtils.FetchDataFieldCallback<Employee>() {
                    @Override
                    public void onCallback(List<Employee> itemList, List<String> itemIdList) {
                        // Update the adapter inside the callback function
                        employeeAdapter.updateData(itemList, itemIdList);
                        employeeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        // Set OnClickListener for the CreateEmployeeButton
        findViewById(R.id.CreateSellPoint).setOnClickListener(v -> {
            // Launch the CreateEmployee activity
            Intent intent = new Intent(EmployeesList.this, CreateEmployee.class);
            startActivity(intent);
        });

    }

}
