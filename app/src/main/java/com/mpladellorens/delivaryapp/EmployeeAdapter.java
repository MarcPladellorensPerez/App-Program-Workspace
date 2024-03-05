package com.mpladellorens.delivaryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;
    private Context context;

    public EmployeeAdapter(List<Employee> employeeList, Context context) {
        this.employeeList = employeeList;
        this.context = context;
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

        // Set data to the views in the ViewHolder
        holder.nameTextView.setText(employee.getName());
        holder.surnameTextView.setText(employee.getSurname());
        holder.phoneTextView.setText(employee.getPhone());
        holder.emailTextView.setText(employee.getEmail());
        holder.adminCheckBox.setChecked(employee.isAdmin());

        // Set OnClickListener for the Edit button (You can handle the click event as per your requirements)
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Edit button click
                // You can open an editing activity, show a dialog, etc.
            }
        });

        // Set OnClickListener for the Delete button (You can handle the click event as per your requirements)
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Delete button click
                // You can show a confirmation dialog, delete the item, etc.
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    // Método para actualizar los datos del adaptador
    public void updateData(List<Employee> newEmployeeList) {
        this.employeeList.clear();
        this.employeeList.addAll(newEmployeeList);
        notifyDataSetChanged();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView surnameTextView;
        TextView phoneTextView;
        TextView emailTextView;
        CheckBox adminCheckBox;
        Button editButton;
        Button deleteButton;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.Name);
            surnameTextView = itemView.findViewById(R.id.surname);
            phoneTextView = itemView.findViewById(R.id.Telephone);
            emailTextView = itemView.findViewById(R.id.Email);
            adminCheckBox = itemView.findViewById(R.id.AdminCheckBox);
            editButton = itemView.findViewById(R.id.Edit);
            deleteButton = itemView.findViewById(R.id.button2);

        }

    }
}