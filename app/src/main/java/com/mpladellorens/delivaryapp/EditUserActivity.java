package com.mpladellorens.delivaryapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {
private String employeeId; // Add this field to store the employee id
private List<String> routeIds = new ArrayList<>();
private RouteAdapter adapter;
private String userDocId;
private List<route> routes;
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
                DocumentSnapshot businessDocument = task.getResult();
                if (businessDocument.exists()) {
                    List<String> routeIds = (List<String>) businessDocument.get("routes");

                    if (!routeIds.isEmpty()) {
                        userDocId = intent.getStringExtra("EXTRA_USER_DOC_ID"); // Retrieve the document ID

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
                                for (route item : routes) {
                                    // Access the id of the route
                                    String id = item.getId();
                                    // Continue processing the route item as needed
                                }
                                RecyclerView routeIdsRecyclerView = findViewById(R.id.RouteIds);
                                routeIdsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                                // Get the document reference
                                DocumentReference employeeRef = db.collection("Employees").document(userDocId);

                                employeeRef.get().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        DocumentSnapshot employeeDocument = task2.getResult();
                                        if (employeeDocument.exists()) {
                                            List<String> checkedIds = (List<String>) employeeDocument.get("routes");

                                            adapter = new RouteAdapter(new ArrayList<>(), routeIds, userDocId, checkedIds);
                                            routeIdsRecyclerView.setAdapter(adapter);


                                            adapter.updateData(routes);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

// Set an OnClickListener for the save button
        saveButton.setOnClickListener(v -> {
            if (adapter != null) {

                // Get the document reference
                DocumentReference employeeRef = db.collection("Employees").document(userDocId);

                // Get the checked routes from the adapter
                List<String> checkedRoutes = new ArrayList<>();

                if (!adapter.itemCheckedStatus.isEmpty()) {
                    Log.d("Debug3", "Number of routes: " + checkedRoutes.size());

                    for (int i = 0; i < adapter.itemCheckedStatus.size(); i++) {
                        if (adapter.itemCheckedStatus.get(i)) {
                            checkedRoutes.add(adapter.routeIds.get(i));
                        }
                    }
                }

                // Get the updated name, email, and surname from the EditTexts
                String updatedName = nameEditText.getText().toString();
                String updatedSurname = surnameEditText.getText().toString();
                String updatedEmail = emailEditText.getText().toString();
                String updatedPhone = phoneEditText.getText().toString();

                // Create a map of fields to update
                Map<String, Object> updates = new HashMap<>();
                updates.put("routes", checkedRoutes);
                updates.put("name", updatedName);
                updates.put("surname", updatedSurname);
                updates.put("email", updatedEmail);
                updates.put("phone", updatedPhone);


                // Update the fields in the document
                employeeRef.update(updates);
                Intent employeeListIntent = new Intent(EditUserActivity.this, EmployeesList.class);
                startActivity(employeeListIntent);
                finish();
            }
        });
    }
}
