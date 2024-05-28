package com.mpladellorens.delivaryapp;

import android.util.Log;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FirebaseAuthUtilities {
    private FirebaseFirestore db;
    private CollectionReference businessCollection;
    private FirebaseAuth mAuth;
    String businessId;

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
    public void logInEmployee(String username, String businessName, String password, OnCompleteListener<AuthResult> completeListener) {
        // First, check if the business name exists

        db.collection("businesses").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean businessExists = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (businessName.equals(document.getString("name"))) {
                                businessExists = true;
                                businessId = document.getId();
                                break;
                            }
                        }
                        if (businessExists) {
                            // Business exists, now check if the user is in Firebase auth
                            mAuth.signInWithEmailAndPassword(username, password)
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            // User is in Firebase auth, now check if the user's ID is inside the business's array of employee IDs
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                String userId = user.getUid();
                                                DocumentReference businessDocument = db.collection("businesses").document(businessId);
                                                businessDocument.get()
                                                        .addOnSuccessListener(documentSnapshot -> {
                                                            List<String> employeesIds = (List<String>) documentSnapshot.get("EmployeesIds");
                                                            Log.d("userID", userId);

                                                            Log.d("employeesIds", employeesIds.toString());
                                                            if (employeesIds != null && employeesIds.contains(userId)) {
                                                                // User's ID is inside the business's array of employee IDs, log in
                                                                completeListener.onComplete(authTask);
                                                            } else {
                                                                // User's ID is not in the business's array of employee IDs
                                                                completeListener.onComplete(Tasks.forException(new Exception("User's ID is not in the business's array of employee IDs")));
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> completeListener.onComplete(Tasks.forException(e)));
                                            } else {
                                                // User is not in Firebase auth
                                                completeListener.onComplete(Tasks.forException(new Exception("User is not in Firebase auth")));
                                            }
                                        } else {
                                            // User is not in Firebase auth
                                            completeListener.onComplete(Tasks.forException(authTask.getException()));
                                        }
                                    });
                        } else {
                            // Business does not exist
                            completeListener.onComplete(Tasks.forException(new Exception("Business does not exist")));
                        }
                    } else {
                        // Failed to read from businesses collection
                        completeListener.onComplete(Tasks.forException(task.getException()));
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
                        completeListener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }
}
