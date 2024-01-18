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
        db.collection("businesses")
                .whereEqualTo("name", businessName) // Find the business with the specified name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Business found, now try to log in the employee
                        String businessId = task.getResult().getDocuments().get(0).getId();
                        db.collection("businesses")
                                .document(businessId)
                                .collection("employees")
                                .whereEqualTo("email", employeeEmail)
                                .whereEqualTo("password", employeePassword)
                                .get()
                                .addOnCompleteListener(employeeTask -> {
                                    if (employeeTask.isSuccessful() && !employeeTask.getResult().isEmpty()) {
                                        mAuth.signInWithEmailAndPassword(employeeEmail, employeePassword)
                                                .addOnCompleteListener(completeListener);
                                    } else {
                                        // Handle employee login failure (e.g., invalid credentials)
                                        completeListener.onComplete(Tasks.forException(new Exception("Invalid credentials")));
                                    }
                                });
                    } else {
                        // Handle business not found
                        completeListener.onComplete(Tasks.forException(new Exception("Business not found")));
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
