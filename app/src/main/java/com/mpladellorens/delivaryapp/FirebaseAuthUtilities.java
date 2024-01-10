package com.mpladellorens.delivaryapp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthUtilities {
    private FirebaseFirestore db;
    private CollectionReference businessCollection;

    public FirebaseAuthUtilities() {
        db = FirebaseFirestore.getInstance();
    }

    public void registerBusiness(String name, String email, String address, String password, String phone, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("name", name);
        businessData.put("email", email);
        businessData.put("address", address);
        businessData.put("password", password);
        businessData.put("phone", phone);

        // Add the business details to the 'businesses' collection
        db.collection("businesses")
                .document(name) // Use the name as the document ID for the business
                .set(businessData)
                .addOnSuccessListener(documentReference -> {
                    // On successful business registration, create a subcollection for employees
                    String businessId = name;
                    createEmployeesSubcollection(businessId, successListener, failureListener);
                    createProductsSubcollection(businessId, successListener, failureListener);
                    createRestaurantsSubcollection(businessId,successListener,failureListener);
                })
                .addOnFailureListener(failureListener);
    }

    private void createEmployeesSubcollection(String businessId, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        // Create a subcollection "employees" inside the business document
        Map<String, Object> employeeData = new HashMap<>();
        employeeData.put("name", "");
        employeeData.put("surname", "");
        employeeData.put("email", "");
        employeeData.put("phone", "");
        employeeData.put("password", "");
        employeeData.put("admin", "n"); // Default value for admin field

        db.collection("businesses").document(businessId)
                .collection("employees")
                .add(employeeData)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
    private void createProductsSubcollection(String productId, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        // Create a subcollection "employees" inside the business document
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "");
        productData.put("Category", "");
        productData.put("Description", "");
        productData.put("Price", "");
        productData.put("Stock", "");

        db.collection("businesses").document(productId)
                .collection("products")
                .add(productData)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
    private void createRestaurantsSubcollection(String restaurantId, OnSuccessListener<DocumentReference> successListener, OnFailureListener failureListener) {
        // Create a subcollection "employees" inside the business document
        Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("Name", "");
        restaurantData.put("Location", "");
        restaurantData.put("AutoVending", "");

        db.collection("businesses").document(restaurantId)
                .collection("Restaurants")
                .add(restaurantData)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
