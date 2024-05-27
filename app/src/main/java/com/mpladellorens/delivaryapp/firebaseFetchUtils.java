package com.mpladellorens.delivaryapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class firebaseFetchUtils {
    public interface FetchDataBusinessCallback {
        void onCallback(List<String> employeeIds);
    }

    public void fetchBusinessdData(String userLoginId,String listId, FetchDataBusinessCallback callback) {
        DocumentReference businessRef = FirebaseFirestore.getInstance().document("businesses/" + userLoginId);

        businessRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> idList = (List<String>) document.get(listId);
                        callback.onCallback(idList); // Pass the data to the callback
                    } else {
                        Log.d("FetchU3213tils", "No such document for user ID: " + userLoginId);
                    }
                } else {
                    Log.d("FetchUtils", "Failed to fetch document for user ID: " + userLoginId, task.getException());
                }
            }
        });
    }
    public interface FetchDataFieldCallback<T> {
        void onCallback(List<T> itemList, List<String> itemIdList);
    }

    public <T> void fetchFieldData(List<String> listIds, String collection, Class<T> classtemplate, FetchDataFieldCallback<T> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

        for (String itemId : listIds) {
            DocumentReference itemRef = db.document(collection+"/" + itemId);
            tasks.add(itemRef.get());
        }

        Task<List<DocumentSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

        allTasks.addOnCompleteListener(new OnCompleteListener<List<DocumentSnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<DocumentSnapshot>> task) {
                if (task.isSuccessful()) {
                    List<T> itemList = new ArrayList<>();
                    List<String> itemIdList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            T item = document.toObject(classtemplate);
                            if (item != null) {
                                itemList.add(item);
                                itemIdList.add(document.getId());
                            }
                        }
                    }

                    // Call the callback function with the fetched data
                    callback.onCallback(itemList, itemIdList);
                } else {
                    Log.d("ListFetch", "get failed with ", task.getException());
                }
            }
        });

    }
    public void fetchEmployeedData(String userLoginId, FetchDataBusinessCallback callback) {
        DocumentReference businessRef = FirebaseFirestore.getInstance().document("Employees/" + userLoginId);

        businessRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> idList = (List<String>) document.get("routesIds");
                        callback.onCallback(idList); // Pass the data to the callback
                    } else {
                        Log.d("FetchU3213tils", "No such document for user ID: " + userLoginId);
                    }
                } else {
                    Log.d("FetchUtils", "Failed to fetch document for user ID: " + userLoginId, task.getException());
                }
            }
        });
    }
    public interface FetchDataListCallback {
        void onCallback(List<String> list);
    }

    public void fetchOtherData(String itemId, String collection, String listId, FetchDataListCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(collection).document(itemId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> list = (List<String>) document.get(listId);
                        callback.onCallback(list); // Pass the data to the callback
                    } else {
                        Log.d("FetchUtils", "No such document in collection: " + collection + " with ID: " + itemId);
                    }
                } else {
                    Log.d("FetchUtils", "Failed to fetch document in collection: " + collection + " with ID: " + itemId, task.getException());
                }
            }
        });
    }

}