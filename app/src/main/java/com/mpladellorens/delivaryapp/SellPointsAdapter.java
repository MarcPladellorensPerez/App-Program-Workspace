package com.mpladellorens.delivaryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SellPointsAdapter extends RecyclerView.Adapter<SellPointsAdapter.SellPointViewHolder> {
    private final List<String> userSellPoints;
    private final Context context;
    private List<SellPoint> sellPoints;
    public List<Boolean> itemCheckedStatus; // This list will keep track of the checked status
    public List<Boolean> itemCheckedStatus2;
    public List<String> sellPointIds;
    private String employeeId;
    private int layoutId;
    private int layoutType;
    public boolean deleteMode =false;
    private final RecyclerView recyclerView;


    public SellPointsAdapter(List<SellPoint> sellPoints, List<String> sellPointIds, String employeeId, List<String> checkedIds, int layoutId, Context context, RecyclerView recyclerView) {
        this.sellPoints = sellPoints;
        this.sellPointIds = sellPointIds;
        this.employeeId = employeeId;
        this.userSellPoints = checkedIds;
        this.layoutId = layoutId;
        if (this.layoutId == R.layout.item) {
            this.layoutType = 1;
        } else if (this.layoutId == R.layout.sellpoint_template) {
            this.layoutType = 2;
        } else if (this.layoutId == R.layout.sell_point_start_route) {
            this.layoutType = 3;
        }
        this.itemCheckedStatus = new ArrayList<>(Collections.nCopies(sellPoints.size(), false));
        this.itemCheckedStatus2 = new ArrayList<>(Collections.nCopies(sellPoints.size(), false));
        this.context = context;
        this.recyclerView = recyclerView;  // Initialize recyclerView here
        Log.d("aksjlkagsjhkjhb",itemCheckedStatus2.toString());
    }
    public void updateData(List<SellPoint> newSellPointList, List<String> newSellPointIdList) {
        this.sellPoints.clear();
        this.sellPoints.addAll(newSellPointList);
        this.sellPointIds.clear();
        this.sellPointIds.addAll(newSellPointIdList);
        itemCheckedStatus2 = new ArrayList(Collections.nCopies(newSellPointList.size(), false));

        notifyDataSetChanged();
        Log.d("aksjlkagsjhkjhb",itemCheckedStatus2.toString());
    }

    @Override
    public SellPointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new SellPointViewHolder(view, layoutType, sellPoints);
    }

    @Override
    public void onBindViewHolder(SellPointViewHolder holder, int position) {
        SellPoint sellPoint = sellPoints.get(position);
        holder.sellPointName.setText(sellPoint.getName());
        if (layoutType == 1) {
            // Get the document ID of the sell point
            String sellPointId = sellPointIds.get(position);
            SellPointIdsSingleton.getInstance().setSellPointIds(sellPointIds);
            // Remove the OnCheckedChangeListener
            holder.CheckBox.setOnCheckedChangeListener(null);

            // Check if the user already has the sell point ID
            if (userSellPoints != null && userSellPoints.contains(sellPointId)) {
                // If the user already has the sell point ID, set the checkbox as checked
                holder.CheckBox.setChecked(true);
                itemCheckedStatus.set(position, true);
            } else {
                holder.CheckBox.setChecked(false);
                itemCheckedStatus.set(position, false);
            }

            // Add the OnCheckedChangeListener back
            holder.CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                itemCheckedStatus.set(position, isChecked);
                Log.d("itemCheckedStatus3", itemCheckedStatus.toString());

                // Save itemCheckedStatus to the Singleton
                CheckedStatusSingleton.getInstance().setItemCheckedStatus(itemCheckedStatus);
            });
        } else if (layoutType == 2) {
            holder.sellPointDescription.setText(sellPoint.getDescription());
            holder.checkBox3.setOnCheckedChangeListener((buttonView, isChecked) -> {
                itemCheckedStatus2.set(position, isChecked);
                CheckedItemsSingleton.getInstance().setItemCheckedStatus2(itemCheckedStatus2);
                Log.d("itemCheckedStatus3", itemCheckedStatus2.toString());
            });

        } else if(layoutType == 3){
            holder.sellPointName.setText(sellPoint.getName());

        }

    }



    @Override
    public int getItemCount() {
        return sellPoints.size();
    }

    public static class SellPointViewHolder extends RecyclerView.ViewHolder {
        TextView sellPointName;
        TextView sellPointDescription;
        CheckBox checkBox3;
        CheckBox CheckBox;
        List<SellPoint> sellPoints;

        public SellPointViewHolder(View itemView, int layoutType, List<SellPoint> sellPoints) {
            super(itemView);
            this.sellPoints = sellPoints;
            if (layoutType == 1) {
                sellPointName = itemView.findViewById(R.id.itemName);
                CheckBox = itemView.findViewById(R.id.CheckBox);
            } else if (layoutType == 2) {
                sellPointName = itemView.findViewById(R.id.name);
                sellPointDescription = itemView.findViewById(R.id.description);
                checkBox3 = itemView.findViewById(R.id.checkBox3);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SellPoint clickedSellPoint = sellPoints.get(getAdapterPosition());
                        Intent intent = new Intent(v.getContext(), editSellPoints.class);
                        intent.putExtra("EXTRA_SELLPOINT", clickedSellPoint); // Pass the SellPoint object
                        intent.putExtra("EXTRA_USER_DOC_ID", clickedSellPoint.getId()); // Pass the document ID
                        Log.d("sellPointOnclick",clickedSellPoint.getId());
                        v.getContext().startActivity(intent);
                    }
                });
            } else if(layoutType == 3){
                sellPointName = itemView.findViewById(R.id.SellPointName);
            }
        }


    }
    public void enterDeleteMode() {
        deleteMode = true;

        // Show the confirm button
        Button confirmButton = ((Activity) context).findViewById(R.id.ConfirmDeleteSellPoints);
        confirmButton.setVisibility(View.VISIBLE);

        // Show the checkboxes
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View itemView = recyclerView.getChildAt(i);
            CheckBox checkBox = itemView.findViewById(R.id.checkBox3);
            checkBox.setVisibility(View.VISIBLE);
        }

        notifyDataSetChanged();
    }

    public void exitDeleteMode() {
        deleteMode = false;

        // Hide the confirm button
        Button confirmButton = ((Activity) context).findViewById(R.id.ConfirmDeleteSellPoints);
        confirmButton.setVisibility(View.GONE);

        // Hide the checkboxes
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View itemView = recyclerView.getChildAt(i);
            CheckBox checkBox = itemView.findViewById(R.id.checkBox3);
            checkBox.setVisibility(View.GONE);
        }

        notifyDataSetChanged();
    }
}