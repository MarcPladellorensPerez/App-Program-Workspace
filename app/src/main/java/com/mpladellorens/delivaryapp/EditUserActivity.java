package com.mpladellorens.delivaryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EditUserActivity extends AppCompatActivity {
    private String employeeId; // Add this field to store the employee id
    private List<String> routeIds = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        // Get the Employee from the Intent
        Intent intent = getIntent();
        Employee employee = (Employee) intent.getSerializableExtra("EXTRA_EMPLOYEE");


        // Initialize your views
        Button saveButton = findViewById(R.id.Save);

        EditText surnameEditText = findViewById(R.id.SurnameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText phoneEditText = findViewById(R.id.PhoneEditText);
        EditText nameEditText = findViewById(R.id.NameEditText);
        // Set the views with the Employee data
        nameEditText.setText(employee.getName());
        surnameEditText.setText(employee.getSurname());
        phoneEditText.setText(employee.getPhone());
        emailEditText.setText(employee.getEmail());




        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DocumentReference businessRef = db.collection("businesses").document(user.getUid());
            businessRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> routeIds = (List<String>) document.get("routes");
                        if (!routeIds.isEmpty()) {
                            Log.d("Debug", "Number of routes: " + routeIds.size());
                            String userDocId = intent.getStringExtra("EXTRA_USER_DOC_ID"); // Retrieve the document ID

                            CollectionReference routesRef = db.collection("Routes");
                            routesRef.whereIn(FieldPath.documentId(), routeIds).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    List<route> routes = task1.getResult().toObjects(route.class);
                                    Log.d("Debug", "Number of routes: " + routes.size());
                                    RecyclerView routeIdsRecyclerView = findViewById(R.id.RouteIds);
                                    routeIdsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                                    RouteAdapter adapter = new RouteAdapter(new ArrayList<>(), routeIds, userDocId);
                                    routeIdsRecyclerView.setAdapter(adapter);
                                    // Update the adapter data
                                    adapter.updateData(routes);
                                }
                            });

                        }
                    }
                }
            });

        }
    }
}