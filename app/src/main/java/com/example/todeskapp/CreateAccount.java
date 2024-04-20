package com.example.todeskapp;


import androidx.appcompat.app.AppCompatActivity;

import com.example.todeskapp.databinding.AccountChoiceBinding;
import com.example.todeskapp.databinding.CreateAccountBinding;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class CreateAccount extends AppCompatActivity{


    private CreateAccountBinding binding;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button createAccountButton;
    private FirebaseAuth mAuth;

    private String accessCode;

    private FirebaseFirestore db;


    static class OrganizerUser {
        String email, defaultValue;

        OrganizerUser(String email, String defaultValue) {
            this.email = email;
            this.defaultValue = defaultValue;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CreateAccountBinding.inflate(getLayoutInflater()); // Initialize the binding
        setContentView(binding.getRoot());
        // Initialize Firebase Authentication

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_password);
        confirmPasswordEditText = findViewById(R.id.input_password_conf);
        createAccountButton = findViewById(R.id.create);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    /* Checks if user logged in on START */
    /*@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }

     */
    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        updateFirestore(email);  // Update Firestore with the email and using the received access code
                        Toast.makeText(CreateAccount.this, "Authentication successful.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateAccount.this, "Authentication failed: " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFirestore(String email) {

        if (accessCode == null) {
            Toast.makeText(this, "Error: No access code provided.", Toast.LENGTH_LONG).show();
            return;
        }
        db.collection("AccessCodes").document(accessCode)
                .update("OrganizerEmail", email)
                .addOnSuccessListener(aVoid -> Toast.makeText(CreateAccount.this, "Firestore updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(CreateAccount.this, "Error updating Firestore: " + e.toString(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateForm() {
        boolean valid = true;
        // Existing validation code
        return valid;
    }


}
