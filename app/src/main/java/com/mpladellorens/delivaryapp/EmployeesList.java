package com.mpladellorens.delivaryapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EmployeesList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    String userLoginId;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_list);

        recyclerView = findViewById(R.id.RecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirmButton = findViewById(R.id.confirmButton);

        // Initialize the adapter with an empty list at the beginning
        employeeAdapter = new EmployeeAdapter(new ArrayList<>(), new ArrayList<>(), EmployeesList.this, recyclerView);
        recyclerView.setAdapter(employeeAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(getString(R.string.userId_key), null);
        if (userLoginId == null) {
            Log.d("EmployeesList", "userId is null");
        } else {
            Log.d("EmployeesList23", "userId: " + userLoginId);
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
        findViewById(R.id.confirmButton).setOnClickListener(v -> {
                    List<String> checkedItemIds = employeeAdapter.getCheckedItemIds();
                    deleteUtils deleteUtils = new deleteUtils(EmployeesList.this);
                    deleteUtils.deleteData(checkedItemIds, "Employees","EmployeesIds" , false,false,new deleteUtils.DeleteDataCallback(){
                        @Override
                        public void onCallback() {

                        }

                        @Override
                        public void onSuccess(Void aVoid) {
                            // After deleting the data, fetch the updated list
                            fetchUtils.fetchBusinessdData(userLoginId, "EmployeesIds", new firebaseFetchUtils.FetchDataBusinessCallback() {
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
                            setSelectionMode(false);
                        }
                    });
                });


        // Set OnClickListener for the CreateEmployeeButton
        findViewById(R.id.CreateEmployee).setOnClickListener(v -> {
            // Launch the CreateEmployee activity
            Intent intent = new Intent(EmployeesList.this, CreateEmployee.class);
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
    void setSelectionMode(boolean selectionMode) {
        employeeAdapter.selectionMode = selectionMode;
        employeeAdapter.notifyDataSetChanged();

        if (selectionMode) {
            confirmButton.setVisibility(View.VISIBLE);
        } else {
            confirmButton.setVisibility(View.GONE);
        }
    }

}
