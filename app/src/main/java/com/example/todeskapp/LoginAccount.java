package com.example.todeskapp;


import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.LoginAccountBinding;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginAccount extends AppCompatActivity{

    private LoginAccountBinding binding;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String accessCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = LoginAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        
        emailEditText = binding.inputEmail;
        passwordEditText = binding.inputPassword;
        loginButton = binding.button;

        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    private void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginAccount.this, "Email and password are required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User is successfully logged in
                        updateFirestore(email, password);

                        Toast.makeText(LoginAccount.this, "Login successful.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginAccount.this, ConfigureTournament.class);
                        intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
                        startActivity(intent);

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginAccount.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        updates.put("OrganizerPassword", password);

        db.collection("AccessCodes").document(accessCode)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(LoginAccount.this, "Firestore updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(LoginAccount.this, "Error updating Firestore: " + e.toString(), Toast.LENGTH_SHORT).show());
    }
}

