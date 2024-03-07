package com.mpladellorens.delivaryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        recyclerView = findViewById(R.id.RecycleView); // Replace with your actual RecyclerView ID
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

        fetchEmployeeData(userLoginId);
        // Set OnClickListener for the CreateEmployeeButton
        findViewById(R.id.CreateEmployee).setOnClickListener(v -> {
            // Launch the CreateEmployee activity
            Intent intent = new Intent(EmployeesList.this, CreateEmployee.class);
            startActivity(intent);
        });
    }

    private void fetchEmployeeData(String userLoginId) {
        DocumentReference businessRef = FirebaseFirestore.getInstance().document("businesses/" + userLoginId);

        businessRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> employeeIds = (List<String>) document.get("EmployeesIds");

                        // Once you have the list of employee IDs, fetch employee data
                        if (employeeIds != null && !employeeIds.isEmpty()) {
                            fetchEmployeesData(employeeIds);
                        }
                    } else {
                        Log.d("EmployeesList", "No such document for user ID: " + userLoginId);                    }
                } else {
                    Log.d("EmployeesList", "get failed with ", task.getException());
                }
            }
        });
    }

    private void fetchEmployeesData(List<String> employeeIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        for (String employeeId : employeeIds) {
            DocumentReference employeeRef = db.document("Employees/" + employeeId);
            tasks.add(employeeRef.get());
        }

        Task<List<DocumentSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

        allTasks.addOnCompleteListener(new OnCompleteListener<List<DocumentSnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<DocumentSnapshot>> task) {
                if (task.isSuccessful()) {
                    List<Employee> employeeList = new ArrayList<>();
                    List<String> employeeIdList = new ArrayList<>(); // List to store the IDs
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Employee employee = document.toObject(Employee.class);
                            if (employee != null) {
                                employeeList.add(employee);
                                employeeIdList.add(document.getId()); // Add the document ID to the list
                            }
                        }
                    }

                    // Update the data and IDs of the adapter and notify the RecyclerView that the data has changed
                    employeeAdapter.updateData(employeeList, employeeIdList); // Assuming you have a method to update the IDs
                    employeeAdapter.notifyDataSetChanged();
                } else {
                    Log.d("EmployeesList", "get failed with ", task.getException());
                }
            }
        });
    }
}
