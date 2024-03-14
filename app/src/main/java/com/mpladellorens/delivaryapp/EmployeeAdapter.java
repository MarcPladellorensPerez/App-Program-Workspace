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

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;
    private List<String> employeeIdList; // List to store the IDs
    private Context context;

    public EmployeeAdapter(List<Employee> employeeList, List<String> employeeIdList, Context context) {
        this.employeeList = employeeList;
        this.employeeIdList = employeeIdList; // Initialize the ID list
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


        // Set OnClickListener for the Edit button (You can handle the click event as per your requirements)


        // Set OnClickListener for the Delete button (You can handle the click event as per your requirements)

    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    // Método para actualizar los datos del adaptador
    public void updateData(List<Employee> newEmployeeList, List<String> newEmployeeIdList) {
        this.employeeList.clear();
        this.employeeList.addAll(newEmployeeList);

        this.employeeIdList.clear(); // Assuming you have a employeeIdList in your adapter
        this.employeeIdList.addAll(newEmployeeIdList);

        notifyDataSetChanged();
    }
        public class EmployeeViewHolder extends RecyclerView.ViewHolder {
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


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Employee employee = employeeList.get(position);
                            String userDocId = employeeIdList.get(position); // Get the document ID

                            Intent intent = new Intent(v.getContext(), EditUserActivity.class);
                            intent.putExtra("EXTRA_EMPLOYEE", employee); // Pass the Employee object
                            intent.putExtra("EXTRA_USER_DOC_ID", userDocId); // Pass the document ID
                            v.getContext().startActivity(intent);
                        }
                    }
                });

            }

        }

}
