package com.mpladellorens.delivaryapp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthUtilities {
    private FirebaseAuth mAuth;

    public FirebaseAuthUtilities() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signInWithEmailAndPassword(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

}