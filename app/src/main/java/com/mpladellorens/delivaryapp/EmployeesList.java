package com.mpladellorens.delivaryapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmployeesList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_list);

        recyclerView = findViewById(R.id.RecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(employeeList, this);
        recyclerView.setAdapter(employeeAdapter);

        // Retrieve current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the current user's ID
            String userId = currentUser.getUid();

            // Reference to the "businesses" collection
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("businesses")
                    .document(userId)
                    .collection("employees")
                    .orderBy("name", Query.Direction.ASCENDING) // You can change the ordering if needed
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Populate employeeList with data from Firestore
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Employee employee = document.toObject(Employee.class);
                                employeeList.add(employee);
                            }
                            // Notify the adapter that the data set has changed
                            employeeAdapter.notifyDataSetChanged();
                        } else {
                            // Handle errors
                        }
                    });
        }

        // Set OnClickListener for the CreateEmployeeButton
        findViewById(R.id.CreateEmployee).setOnClickListener(v -> {
            // Launch the CreateEmployee activity
            Intent intent = new Intent(EmployeesList.this, CreateEmployee.class);
            startActivity(intent);
        });
    }
}