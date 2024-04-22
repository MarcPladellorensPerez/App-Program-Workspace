package com.mpladellorens.delivaryapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.res.TypedArrayUtils.getString;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class deleteUtils {

    private final FirebaseFirestore db;
    private final Context context;

    public deleteUtils(Context context) {
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void deleteData(List<String> ids, String collection,String field,Boolean routes, Boolean sellPoints, DeleteDataCallback callback) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        String userLoginId = sharedPreferences.getString(context.getString(R.string.userId_key), null);
        firebaseFetchUtils fetchUtils= new firebaseFetchUtils();

        for (String id : ids) {
            db.collection(collection).document(id)
                    .delete()
                    .addOnSuccessListener(callback)
                    .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
        }

        // Update the "EmployeesIds" field in the "businesses" collection
        db.collection("businesses").document(userLoginId)
                .update(field, FieldValue.arrayRemove(ids.toArray()))
                .addOnSuccessListener(callback)
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

        if (routes) {
            fetchUtils.fetchBusinessdData(userLoginId, "EmployeesIds", new firebaseFetchUtils.FetchDataBusinessCallback() {
                @Override
                public void onCallback(List<String> employeeIds) {
                    for (String employeeId : employeeIds) {
                        // Get a reference to the employee document
                        DocumentReference employeeDocument = db.collection("Employees").document(employeeId);

                        // Update the "routesIds" field in the employee document
                        employeeDocument.update("routesIds", FieldValue.arrayRemove(ids.toArray()))
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully removed IDs from routesIds"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error removing IDs from routesIds", e));
                    }
                }
            });
        }

        if (sellPoints) {
            fetchUtils.fetchBusinessdData(userLoginId, "routes", new firebaseFetchUtils.FetchDataBusinessCallback() {
                @Override
                public void onCallback(List<String> routeIds) {
                    for (String routeId : routeIds) {
                        // Get a reference to the route document
                        DocumentReference routeDocument = db.collection("Routes").document(routeId);

                        // Update the "sellPointsIds" field in the route document
                        routeDocument.update("SellPointsIdList", FieldValue.arrayRemove(ids.toArray()))
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully removed IDs from sellPointsIds"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error removing IDs from sellPointsIds", e));
                    }
                }
            });
        }
    }

    public static abstract class DeleteDataCallback implements OnSuccessListener<Void> {
        public abstract void onCallback();
    }
}
