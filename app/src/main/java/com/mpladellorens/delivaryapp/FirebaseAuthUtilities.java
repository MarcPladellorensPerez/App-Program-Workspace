package com.mpladellorens.delivaryapp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.android.gms.tasks.Task;

public class FirebaseAuthUtilities {
    private FirebaseFirestore db;
    private CollectionReference businessCollection;
    private FirebaseAuth mAuth;

    public FirebaseAuthUtilities() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
    public void registerBusiness(String name, String email, String address, String password, String phone,
                                 List<String> employeesIds, List<String> salePoints, List<List<String>> routes,
                                 OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Use the user's UID as the document ID
                            String userId = user.getUid();

                            // Create a reference to the "businesses" collection
                            DocumentReference businessDocument = db.collection("businesses").document(userId);

                            Map<String, Object> businessData = new HashMap<>();
                            businessData.put("name", name);
                            businessData.put("email", email);
                            businessData.put("address", address);
                            businessData.put("phone", phone);
                            businessData.put("EmployeesIds", employeesIds);
                            businessData.put("salePointsIds", salePoints);
                            businessData.put("routes", routes);

                            businessDocument.set(businessData)
                                    .addOnSuccessListener(aVoid -> {
                                        // Notify success
                                        successListener.onSuccess(null);
                                    })
                                    .addOnFailureListener(failureListener);
                        }
                    } else {
                        failureListener.onFailure(task.getException());
                    }
                });
    }






    public void loginEmployee(String businessName, String employeeEmail, String employeePassword, OnCompleteListener<AuthResult> completeListener) {
        // First, check if the employee exists
        db.collection("Employees")
                .whereEqualTo("email", employeeEmail)
                .get()
                .addOnCompleteListener(employeeTask -> {
                    if (employeeTask.isSuccessful() && !employeeTask.getResult().isEmpty()) {
                        // Employee found, now get the employeeId
                        String employeeId = employeeTask.getResult().getDocuments().get(0).getId();

                        // Now, check if the business exists
                        db.collection("businesses")
                                .whereEqualTo("name", businessName)
                                .get()
                                .addOnCompleteListener(businessTask -> {
                                    if (businessTask.isSuccessful() && !businessTask.getResult().isEmpty()) {
                                        // Business found, now get the EmployeesIds
                                        List<String> employeesIds = (List<String>) businessTask.getResult().getDocuments().get(0).get("EmployeesIds");

                                        // Check if the employeeId is in the EmployeesIds
                                        if (employeesIds != null && employeesIds.contains(employeeId)) {
                                            // Employee is associated with the business, proceed with the login
                                            mAuth.signInWithEmailAndPassword(employeeEmail, employeePassword)
                                                    .addOnCompleteListener(completeListener);
                                        } else {
                                            // Employee is not associated with the business
                                            completeListener.onComplete(Tasks.forException(new Exception("Employee is not associated with the business")));
                                        }
                                    } else {
                                        // Business not found
                                        completeListener.onComplete(Tasks.forException(new Exception("Business not found")));
                                    }
                                });
                    } else {
                        // Employee not found
                        completeListener.onComplete(Tasks.forException(new Exception("Employee not found")));
                    }
                });
    }
    public void loginBusiness(String email, String password, OnCompleteListener<AuthResult> completeListener) {
        // Use Firebase Authentication for business login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Business login successful
                        completeListener.onComplete(task);
                    } else {
                        // Business login failed
                        completeListener.onComplete(Tasks.forException(task.getException()));                    }
                });
    }
}
