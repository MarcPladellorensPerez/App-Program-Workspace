package com.mpladellorens.delivaryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class MainActivity extends AppCompatActivity {
    private EditText userEmailEditText;
    private EditText userPasswordEditText;
    private Button userLogInButton;
    private EditText businessEmailEditText;
    private EditText businessPasswordEditText;
    private Button businessLogInButton;
    private Button registerButton;
    private FirebaseAuthUtilities authUtilities;
    ConstraintLayout businessLogIn;
    ConstraintLayout normalLogIn;
    Button businessButton;
    Button userButton;
    private boolean BusinessUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        businessLogIn = findViewById(R.id.BusinessLogIn);
        businessLogInButton = findViewById(R.id.BusinessLogInButton);
        normalLogIn = findViewById(R.id.NormalLogIn);
        businessButton = findViewById(R.id.BusinessUser);
        userButton = findViewById(R.id.NormalUser);
        userEmailEditText = findViewById(R.id.UserEmail);
        userPasswordEditText = findViewById(R.id.UserPassword);
        userLogInButton = findViewById(R.id.NormalLogInButton);
        registerButton = findViewById(R.id.SignInButton);
        authUtilities = new FirebaseAuthUtilities();
        businessPasswordEditText =findViewById(R.id.BusinessPassword);
        businessEmailEditText =findViewById(R.id.BusinessEmail);

        businessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessLogIn.setVisibility(View.VISIBLE);
                normalLogIn.setVisibility(View.GONE);
                BusinessUser = true;
            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessLogIn.setVisibility(View.GONE);
                normalLogIn.setVisibility(View.VISIBLE);
                BusinessUser = false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        userLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmailEditText.getText().toString();
                String password = userPasswordEditText.getText().toString();
                Log.d("TAG", "click");
                authUtilities.loginEmployee("businessName", email, password, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Login successful");
                            Intent intent = new Intent(MainActivity.this, EmployeesList.class);
                            startActivity(intent);
                        } else {
                            Log.e("TAG", "Login not successful", task.getException());
                        }
                    }
                });
            }
        });
        businessLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = businessEmailEditText.getText().toString();
                String password = businessPasswordEditText.getText().toString();
                Log.d("TAG", "click");
                authUtilities.loginBusiness(email, password, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Login successful");
                            Intent intent = new Intent(MainActivity.this, MainMenu.class);
                            startActivity(intent);
                        } else {
                            Log.e("TAG", "Login not successful", task.getException());
                        }
                    }
                });
            }
        });
    }
}