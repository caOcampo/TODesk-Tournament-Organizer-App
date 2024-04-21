package com.example.todeskapp;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.CreateAccountBinding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccount extends AppCompatActivity {

    private CreateAccountBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String accessCode;

    private ExecutorService executorService;  // Executor service to run tasks in the background

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();  // Initialize the executor service

        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        binding.create.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString();
            String password = binding.inputPassword.getText().toString();
            createAccount(email, password);
        });
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        // Using ExecutorService to perform Firebase authentication on a background thread
        executorService.execute(() -> {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            runOnUiThread(() -> {
                                updateFirestore(email, password);
                                Toast.makeText(CreateAccount.this, "Authentication successful.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(this, ConfigureTournament.class);
                                intent.putExtra("ACCESS_CODE", accessCode);
                                startActivity(intent);
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(CreateAccount.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
        });
    }

    private void updateFirestore(String email, String password) {
        if (accessCode == null) {
            Toast.makeText(this, "Error: No access code provided.", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("OrganizerEmail", email);
        updates.put("OrganizerPassword", password);

        executorService.execute(() -> {
            db.collection("AccessCodes").document(accessCode)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> runOnUiThread(() -> Toast.makeText(CreateAccount.this, "Firestore updated successfully.", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(CreateAccount.this, "Error updating Firestore: " + e.toString(), Toast.LENGTH_SHORT).show()));
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();
        String confirmPassword = binding.inputPasswordConf.getText().toString();

        if (email.isEmpty()) {
            binding.inputEmail.setError("Required.");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.inputPassword.setError("Required.");
            valid = false;
        } else {
            binding.inputPassword.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            binding.inputPasswordConf.setError("Passwords do not match.");
            valid = false;
        } else {
            binding.inputPasswordConf.setError(null);
        }

        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the executor service when the activity is destroyed
    }
}