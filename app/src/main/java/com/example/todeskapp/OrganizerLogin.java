package com.example.todeskapp;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.LoginAccountBinding;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class OrganizerLogin extends AppCompatActivity{

    private LoginAccountBinding binding;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String accessCode;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LoginAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();

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
            Toast.makeText(this, "Email and password are required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkAccountDetails(email, password);
                    } else {
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAccountDetails(String email, String password) {
        executorService.execute(() -> {
            db.collection("AccessCodes").document(accessCode)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            String authEmail = documentSnapshot.getString("OrganizerEmail");
                            String authPassword = documentSnapshot.getString("OrganizerPassword");

                            if (email.equals(authEmail) && password.equals(authPassword)) {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
                                    navigateToConfigureTournament();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Account details are not correct! Try Again!", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "No such access code exists.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .addOnFailureListener(e -> runOnUiThread(() -> {
                        Toast.makeText(this, "Error accessing Firestore: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }));
        });
    }

    private void navigateToConfigureTournament() {
        Intent intent = new Intent(this, ConfigureTournament.class);
        intent.putExtra("ACCESS_CODE", accessCode);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuth != null) mAuth.signOut();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
