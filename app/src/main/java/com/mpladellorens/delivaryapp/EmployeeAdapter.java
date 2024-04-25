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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private final RecyclerView recyclerView;
    private List<Employee> employeeList;
    private List<String> employeeIdList;
    private Context context;
    boolean isSelectionMode = false;
    private List<Boolean> selectionStatus;
    boolean selectionMode = false;



    public EmployeeAdapter(List<Employee> employeeList, List<String> employeeIdList, Context context,RecyclerView recyclerView) {
        this.employeeList = employeeList;
        this.employeeIdList = employeeIdList;
        this.context = context;
        this.selectionStatus = new ArrayList<>(Collections.nCopies(employeeList.size(), false));
        this.recyclerView = recyclerView;

    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_template, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);

        holder.nameTextView.setText(employee.getName());
        holder.surnameTextView.setText(employee.getSurname());
        holder.phoneTextView.setText(employee.getPhone());
        holder.emailTextView.setText(employee.getEmail());

        holder.checkBox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(selectionStatus.get(position));
        CheckBox checkBox = holder.itemView.findViewById(R.id.checkBox2);
        if (selectionMode) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
        holder.checkBox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public void updateData(List<Employee> newEmployeeList, List<String> newEmployeeIdList) {
        this.employeeList.clear();
        this.employeeList.addAll(newEmployeeList);

        this.employeeIdList.clear();
        this.employeeIdList.addAll(newEmployeeIdList);

        this.selectionStatus.clear();
        this.selectionStatus.addAll(Collections.nCopies(newEmployeeList.size(), false));

        notifyDataSetChanged();
    }

    public void enterSelectionMode() {
        isSelectionMode = true;
        notifyDataSetChanged();

        // Show the confirm button
        Button confirmButton = ((Activity) context).findViewById(R.id.confirmButton);
        confirmButton.setVisibility(View.VISIBLE);
    }

    public void exitSelectionMode() {
        isSelectionMode = false;
        selectionStatus = new ArrayList<>(Collections.nCopies(employeeList.size(), false));
        notifyDataSetChanged();

        // Hide the confirm button
        Button confirmButton = ((Activity) context).findViewById(R.id.confirmButton);
        confirmButton.setVisibility(View.GONE);
    }



    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView surnameTextView;
        TextView phoneTextView;
        TextView emailTextView;
        CheckBox checkBox;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.Name);
            surnameTextView = itemView.findViewById(R.id.surname);
            phoneTextView = itemView.findViewById(R.id.Telephone);
            emailTextView = itemView.findViewById(R.id.Email);
            checkBox = itemView.findViewById(R.id.checkBox2);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("EmployeeViewHolder", "Item long clicked");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && isSelectionMode) {
                        selectionStatus.set(position, !selectionStatus.get(position));
                        checkBox.setChecked(selectionStatus.get(position));
                    }
                    return true;
                }
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (isSelectionMode) {
                        selectionStatus.set(position, !selectionStatus.get(position));
                        checkBox.setChecked(selectionStatus.get(position));
                    } else {
                        Employee employee = employeeList.get(position);
                        String userDocId = employeeIdList.get(position);

                        Intent intent = new Intent(v.getContext(), EditUserActivity.class);
                        intent.putExtra("EXTRA_EMPLOYEE", employee);
                        intent.putExtra("EXTRA_USER_DOC_ID", userDocId);
                        v.getContext().startActivity(intent);
                    }
                }
            });

        }
    }
    public List<String> getCheckedItemIds() {
        List<String> checkedItemIds = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            View itemView = recyclerView.getChildAt(i);
            CheckBox checkBox = itemView.findViewById(R.id.checkBox2);
            if (checkBox.isChecked()) {
                checkedItemIds.add(employeeIdList.get(i));
            }
        }
        return checkedItemIds;
    }


}