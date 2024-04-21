package com.example.todeskapp;

import java.util.*;


import androidx.appcompat.app.AppCompatActivity;

import com.example.todeskapp.databinding.CreateAccountBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;



public class CreateAccount extends AppCompatActivity{


    private CreateAccountBinding binding;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String accessCode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CreateAccountBinding.inflate(getLayoutInflater()); // Initialize the binding
        setContentView(binding.getRoot());

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = binding.inputEmail;
        passwordEditText = binding.inputPassword;
        confirmPasswordEditText = binding.inputPasswordConf;
        createAccountButton = binding.create;

        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Pass the password to updateFirestore method
                        updateFirestore(email, password);
                        Toast.makeText(CreateAccount.this, "Authentication successful.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, ConfigureTournament.class);
                        intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
                        startActivity(intent);
                    } else {
                        Toast.makeText(CreateAccount.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFirestore(String email, String password) {

        if (accessCode == null) {
            Toast.makeText(this, "Error: No access code provided.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a map to hold the update data
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("OrganizerEmail", email);
        updates.put("OrganizerPassword", password);  // Store the password

        db.collection("AccessCodes").document(accessCode)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(CreateAccount.this, "Firestore updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(CreateAccount.this, "Error updating Firestore: " + e.toString(), Toast.LENGTH_SHORT).show());

    }

    private boolean validateForm() {
        boolean valid = true;

        // Check for a valid email
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (email.isEmpty()) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        // Check for a valid password
        if (password.isEmpty()) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match.");
            valid = false;
        } else {
            confirmPasswordEditText.setError(null);
        }

        return valid;
    }


}
