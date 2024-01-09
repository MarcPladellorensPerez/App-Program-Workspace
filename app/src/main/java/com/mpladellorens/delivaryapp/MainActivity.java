package com.mpladellorens.delivaryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button logInButton;
    private Button registerButton;
    private FirebaseAuthUtilities authUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.EmailEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);
        logInButton = findViewById(R.id.LogInButton);
        registerButton = findViewById(R.id.SignInButton);
        authUtilities = new FirebaseAuthUtilities();
/*
        logInButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                // Handle empty email or password field
                Toast.makeText(MainActivity.this, "Email or password cannot be empty.", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "Email or password is empty.");
            } else {
                // Call the signInWithEmailAndPassword method from FirebaseAuthUtilities
                authUtilities.signInWithEmailAndPassword(email, password, task -> {
                    if (task.isSuccessful()) {
                        // Sign in successful
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Log.d("MainActivity", "log in successful.");
                        Utilities.goTo(MainActivity.this, MainMenu.class);

                    } else {
                        // Sign in failed
                        Log.d("MainActivity", "wrong user or password.");
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        */

            registerButton.setOnClickListener(view -> {
                // Perform actions when the registerButton is clicked
                // For instance, navigate to the Register activity

                Utilities.goTo(MainActivity.this, Register.class);
            });

        }
}

