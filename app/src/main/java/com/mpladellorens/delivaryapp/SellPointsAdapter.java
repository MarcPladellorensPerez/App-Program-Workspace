package com.mpladellorens.delivaryapp;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class SellPointsAdapter extends RecyclerView.Adapter<SellPointsAdapter.SellPointViewHolder> {

    private List<SellPoint> sellPointList;
    private List<String> sellPointIdList; // List to store the IDs
    private Context context;

    public SellPointsAdapter(List<SellPoint> sellPointList, List<String> sellPointIdList, Context context) {
        this.sellPointList = sellPointList;
        this.sellPointIdList = sellPointIdList;
        this.context = context;
    }

    @NonNull
    @Override
    public SellPointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sellpoint_template, parent, false);
        return new SellPointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellPointViewHolder holder, int position) {
        SellPoint sellPoint = sellPointList.get(position);

        // Set data to the views in the ViewHolder
        holder.nameTextView.setText(sellPoint.getName());
        holder.descriptionTextView.setText(sellPoint.getDescription());

    }

    @Override
    public int getItemCount() {
        return sellPointList.size();
    }

    // Método para actualizar los datos del adaptador
    public void updateData(List<SellPoint> newSellPointList, List<String> newSellPointIdList) {
        this.sellPointList.clear();
        this.sellPointList.addAll(newSellPointList);

        this.sellPointIdList.clear(); // Assuming you have a sellPointIdList in your adapter
        this.sellPointIdList.addAll(newSellPointIdList);

        notifyDataSetChanged();
    }
    public class SellPointViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;


        public SellPointViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name);
            descriptionTextView = itemView.findViewById(R.id.description);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        SellPoint sellPoint = sellPointList.get(position);
                        String userDocId = sellPointIdList.get(position); // Get the document ID

                        Intent intent = new Intent(v.getContext(), editSellPoints.class);
                        intent.putExtra("EXTRA_SELLPOINT", sellPoint); // Pass the SellPoint object
                        intent.putExtra("EXTRA_USER_DOC_ID", userDocId); // Pass the document ID
                        v.getContext().startActivity(intent);
                    }
                }
            });

        }

    }

}