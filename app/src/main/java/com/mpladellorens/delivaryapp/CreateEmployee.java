package com.mpladellorens.delivaryapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateEmployee extends AppCompatActivity {

    private EditText nameEditText, surnameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CheckBox admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_employee);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.NameEditText);
        surnameEditText = findViewById(R.id.SurnameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.PhoneEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);
        confirmPasswordEditText = findViewById(R.id.ConfirmPasswordEditText);
        admin = findViewById(R.id.checkBox);
        Button createEmployeeButton = findViewById(R.id.CreateUser);

        createEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEmployee();
            }
        });
    }

    private void createEmployee() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("Firestore", "Business document ID: " + userId);

            String name = nameEditText.getText().toString().trim();
            String surname = surnameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            boolean isAdmin = admin.isChecked();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(CreateEmployee.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the user in the authentication system
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            // User created successfully in authentication system
                            Toast.makeText(CreateEmployee.this, "User created successfully in authentication", Toast.LENGTH_SHORT).show();

                            // Get the employee ID assigned by Firestore
                            String employeeId = authTask.getResult().getUser().getUid();

                            // Create a reference to the "Employees" collection
                            DocumentReference employeeDocument = db.collection("Employees").document(employeeId);

                            Map<String, Object> employeeData = new HashMap<>();
                            employeeData.put("name", name);
                            employeeData.put("surname", surname);
                            employeeData.put("email", email);
                            employeeData.put("phone", phone);
                            employeeData.put("isAdmin", isAdmin);

                            // Add the employee data to the "Employees" collection
                            employeeDocument.set(employeeData)
                                    .addOnCompleteListener(employeeDataTask -> {
                                        if (employeeDataTask.isSuccessful()) {
                                            // Employee data added successfully
                                            // Add the employee ID to the business's EmployeesIds array
                                            db.collection("businesses")
                                                    .document(userId)
                                                    .update("EmployeesIds", FieldValue.arrayUnion(employeeId))
                                                    .addOnCompleteListener(updateTask -> {
                                                        if (updateTask.isSuccessful()) {
                                                            Toast.makeText(CreateEmployee.this, "Employee created successfully", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(CreateEmployee.this, "Failed to update business with employee ID: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            Log.d("Firestore", "Business document ID: " + userId);
                                                            Log.d("Firestore", "Employee document ID: " + employeeId);
                                                        }
                                                    });
                                        } else {
                                            // Failed to add employee data
                                            Toast.makeText(CreateEmployee.this, "Failed to add employee data: " + employeeDataTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d("Firestore", "Business document ID: " + userId);
                                            Log.d("Firestore", "Employee document ID: " + employeeId);
                                        }
                                    });
                        } else {
                            // Failed to create user in authentication system
                            Toast.makeText(CreateEmployee.this, "Failed to create user in authentication: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Firestore", "Business document ID: " + userId);
                        }
                    });
        }
    }
}