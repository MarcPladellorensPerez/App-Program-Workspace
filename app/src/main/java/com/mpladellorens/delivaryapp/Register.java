package com.mpladellorens.delivaryapp;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText addressEditText = findViewById(R.id.AddressEditText);
        EditText businessNameEditText = findViewById(R.id.BusinessNameEditText);
        EditText phoneNumberEditText = findViewById(R.id.PhoneNumberEditText);
        EditText mailEditText = findViewById(R.id.MailEditText);
        EditText registerPasswordEditText = findViewById(R.id.RegisterPasswordEditText);
        EditText registerConfirmPasswordEditText = findViewById(R.id.RegisterConfirmPasswordEditText);
        Button RegisterButton = findViewById(R.id.RegisterButton);

        db = FirebaseFirestore.getInstance();

        // Implement the registration functionality upon button click or any trigger
        // For example, when the user taps a "Register" button
        // Here, I'm assuming you have a button named 'registerButton' in your layout

        RegisterButton.setOnClickListener(view -> {
            String address = addressEditText.getText().toString();
            String businessName = businessNameEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String email = mailEditText.getText().toString();
            String password = registerPasswordEditText.getText().toString();
            String confirmPassword = registerConfirmPasswordEditText.getText().toString();

            // Perform validation checks here (e.g., check if fields are not empty, passwords match, etc.)
            if (password.equals(confirmPassword)) {
                // If validation passes, proceed with registration
                // Call the registerBusiness method from FirebaseAuthUtilities or FirebaseDbUtilities

                FirebaseAuthUtilities authUtilities = new FirebaseAuthUtilities();
                authUtilities.registerBusiness(businessName, email, address, password, phoneNumber,
                        aVoid -> {
                            // Registration successful
                            Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            // Redirect the user to another screen or perform necessary actions
                            Log.d("Register", "registration succesful.");

                        },
                        e -> {
                            // Registration failed
                            Toast.makeText(Register.this, "Registration failed! Please try again.", Toast.LENGTH_SHORT).show();
                            // Handle failure (e.g., show error message, allow user to retry, etc.)
                            Log.d("Register", "registration failed.");

                        });
            } else {
                // Handle validation errors (e.g., show error messages for invalid input)
                Toast.makeText(Register.this, "Passwords aren't the same.", Toast.LENGTH_SHORT).show();
                Log.d("Register", "passwords arent the same.");

            }
        });
    }
}
