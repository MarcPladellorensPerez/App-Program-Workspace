package com.mpladellorens.delivaryapp;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.Manifest;
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
    private EditText BusinessNameEditText;
    private boolean BusinessUser;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

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
        BusinessNameEditText = findViewById(R.id.BusinessNameEditText);
        Context context = MainActivity.this;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
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
                String businessName= BusinessNameEditText.getText().toString();
                String email = userEmailEditText.getText().toString();
                String password = userPasswordEditText.getText().toString();
                Log.d("TAG", "click");
                authUtilities.logInEmployee(email, businessName, password, new OnCompleteListener<AuthResult>() {
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

                            SharedPreferences sharedPref = context.getSharedPreferences(
                                    getString(R.string.preference_file_name), Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.userId_key), FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
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