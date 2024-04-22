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
import java.util.List;

public class SellPointsAdapter extends RecyclerView.Adapter<SellPointsAdapter.SellPointViewHolder> {
    private final List<String> userSellPoints;
    private List<SellPoint> sellPoints;
    public List<Boolean> itemCheckedStatus; // This list will keep track of the checked status
    public List<String> sellPointIds;
    private String employeeId;
    private int layoutId;
    private int layoutType;

    public SellPointsAdapter(List<SellPoint> sellPoints, List<String> sellPointIds, String employeeId, List<String> checkedIds, int layoutId) {
        this.sellPoints = sellPoints;
        this.sellPointIds = sellPointIds;
        this.employeeId = employeeId;
        this.userSellPoints = checkedIds;
        this.layoutId = layoutId;
        this.layoutType = (layoutId == R.layout.item) ? 1 : 2;
        this.itemCheckedStatus = new ArrayList<>(Collections.nCopies(sellPoints.size(), false));
    }
    public void updateData(List<SellPoint> newSellPointList, List<String> newSellPointIdList) {
        this.sellPoints.clear();
        this.sellPoints.addAll(newSellPointList);

        this.sellPointIds.clear();
        this.sellPointIds.addAll(newSellPointIdList);
        notifyDataSetChanged();
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
        }

    }
    public List<Boolean> getitemCheckedStatus(){
        Log.d("AAAAAA", itemCheckedStatus.toString());
        return itemCheckedStatus;

    }


    @Override
    public int getItemCount() {
        return sellPoints.size();
    }

    public static class SellPointViewHolder extends RecyclerView.ViewHolder {
        TextView sellPointName;
        TextView sellPointDescription;
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
            }
        }


    }

}