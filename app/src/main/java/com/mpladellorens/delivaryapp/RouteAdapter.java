package com.mpladellorens.delivaryapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private final List<String> userRoutes;

    private List<route> routes;
    public List<Boolean> itemCheckedStatus; // This list will keep track of the checked status
    public List<String> routeIds;
    private String employeeId;
    private int layoutId;
    private int layoutType;
    private List<SellPoint> sellPoints; // Add this line

    public RouteAdapter(List<route> routes, List<String> routeIds, String employeeId, List<String> checkedIds, int layoutId) {        this.routes = routes;
        this.routeIds = routeIds;
        this.employeeId = employeeId;
        this.userRoutes = checkedIds;
        this.layoutId = layoutId;
        this.layoutType = (layoutId == R.layout.item) ? 1 : 2;
        this.itemCheckedStatus = new ArrayList<>(Collections.nCopies(routes.size(), false));

    }
    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

        // Pass the routes list to the ViewHolder
        return new RouteViewHolder(view, layoutType, routes);
    }


    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        route route = routes.get(position);
        holder.routeName.setText(route.getName());
        if (layoutType == 1) {
            // Get the document ID of the route
            String routeId = routeIds.get(position);

            // Remove the OnCheckedChangeListener
            holder.CheckBox.setOnCheckedChangeListener(null);

            // Check if the user already has the route ID
            if (userRoutes != null && userRoutes.contains(routeId)) {
                // If the user already has the route ID, set the checkbox as checked
                holder.CheckBox.setChecked(true);
                itemCheckedStatus.set(position, true);
            } else {
                holder.CheckBox.setChecked(false);
                itemCheckedStatus.set(position, false);
            }

            // Add the OnCheckedChangeListener back
            holder.CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                itemCheckedStatus.set(position, isChecked);
                Log.d("itemChecked status", itemCheckedStatus.toString());

            });
        } else if (layoutType == 2) {
            // Assuming that your route class has a getDescription method
            holder.routeDescription.setText(route.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        if (layoutType == 1 || layoutType == 2) {
            return routes.size();
        } else if (layoutType == 3) {
            return sellPoints.size();
        }
        return 0;
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView routeName;
        TextView routeDescription;
        CheckBox CheckBox;
        List<route> routes;

        public RouteViewHolder(View itemView, int layoutType, List<route> routes) {
            super(itemView);
            this.routes = routes;
            if (layoutType == 1) {
                routeName = itemView.findViewById(R.id.itemName);
                CheckBox = itemView.findViewById(R.id.CheckBox);
            } else if (layoutType == 2) {
                routeName = itemView.findViewById(R.id.RouteName);
                routeDescription = itemView.findViewById(R.id.RouteDescription);

                // Set the click listener for the entire item view
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the clicked item
                        route clickedRoute = routes.get(getAdapterPosition());

                        // Create an intent to start the EditRoute activity
                        Intent intent = new Intent(v.getContext(), EditRoute.class);

                        // Pass the route ID to the EditRoute activity
                        intent.putExtra("routeId", clickedRoute.getId());

                        // Start the activity
                        v.getContext().startActivity(intent);
                    }
                });
            }
        }
    }

    public void updateData(List<route> newRoutes) {
        this.routes = newRoutes;
        // Initialize the itemCheckedStatus list with the same size as routes
        // and set all items as unchecked (false)
        this.itemCheckedStatus = new ArrayList<>(Collections.nCopies(routes.size(), false));
        notifyDataSetChanged();
    }


    public void sortData() {
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

    public void updateRouteIds(List<String> newRouteIds) {
        this.routeIds = newRouteIds;
        notifyDataSetChanged();
    }
}