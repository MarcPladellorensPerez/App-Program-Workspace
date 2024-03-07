package com.mpladellorens.delivaryapp;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Handler;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private List<route> routes;
    private List<Boolean> itemCheckedStatus; // This list will keep track of the checked status
    private List<String> routeIds;
    private String employeeId;

    public RouteAdapter(List<route> routes, List<String> routeIds, String employeeId) {
        this.routes = routes;
        this.routeIds = routeIds;
        this.employeeId = employeeId;
        // Initialize the itemCheckedStatus list with the same size as routes
        // and set all items as unchecked (false)
        this.itemCheckedStatus = new ArrayList<>(Collections.nCopies(routes.size(), false));
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_item, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        route route = routes.get(position);
        holder.routeName.setText(route.getName());
        holder.CheckBox.setChecked(itemCheckedStatus.get(position));

        // Set other views in the holder

        holder.CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            itemCheckedStatus.set(position, isChecked);
            sortData();

            // Get the Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Get the document reference
            DocumentReference employeeRef = db.collection("Employees").document(employeeId);

            // Get the document ID of the route
            String routeId = routeIds.get(position);

            if (isChecked) {
                Log.d("Debug", "Checked");

                // Update the routes field in the document
                employeeRef.update("routes", FieldValue.arrayUnion(routeId));
            } else {
                Log.d("Debug", "Unchecked");

                // Update the routes field in the document
                employeeRef.update("routes", FieldValue.arrayRemove(routeId));
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView routeName;
        CheckBox CheckBox;
        // Add other views here

        public RouteViewHolder(View itemView) {
            super(itemView);
            routeName = itemView.findViewById(R.id.routeName);
            CheckBox = itemView.findViewById(R.id.CheckBox);
        }
    }

    public void updateData(List<route> newRoutes) {
        this.routes = newRoutes;
        // Initialize the itemCheckedStatus list with the same size as routes
        // and set all items as unchecked (false)
        this.itemCheckedStatus = new ArrayList<>(Collections.nCopies(routes.size(), false));
        notifyDataSetChanged();
    }

    private void sortData() {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            // Sort the routes and itemCheckedStatus based on the checkbox status
            List<Integer> indices = IntStream.range(0, itemCheckedStatus.size())
                    .boxed()
                    .sorted(Comparator.comparing(itemCheckedStatus::get).reversed())
                    .collect(Collectors.toList());

            List<route> sortedRoutes = new ArrayList<>();
            List<Boolean> sortedItemCheckedStatus = new ArrayList<>();

            for (int index : indices) {
                sortedRoutes.add(routes.get(index));
                sortedItemCheckedStatus.add(itemCheckedStatus.get(index));
            }

            this.routes = sortedRoutes;
            this.itemCheckedStatus = sortedItemCheckedStatus;

            notifyDataSetChanged();
        });
    }
}