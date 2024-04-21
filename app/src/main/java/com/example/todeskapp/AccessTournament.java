package com.example.todeskapp;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.todeskapp.databinding.AccessTournamentBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AccessTournament extends AppCompatActivity {

    private AccessTournamentBinding binding;
    private FirebaseFirestore db;
    private String validAccessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AccessTournamentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessCode = binding.editTextTextPassword.getText().toString();
                checkAccessCode(accessCode);
            }
        });
    }

    private void checkAccessCode(String accessCode) {
        db.collection("AccessCodes")
                .document(accessCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Code exists in the database
                            validAccessCode = accessCode;  // Store the valid code in the accessCode variable
                            Toast.makeText(AccessTournament.this, "Access Code is valid!", Toast.LENGTH_SHORT).show();
                            // Proceed with any other logic now that the code is confirmed, e.g., open new Activity
                        } else {
                            // Code does not exist in the database
                            Toast.makeText(AccessTournament.this, "Invalid Access Code.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Task failed with an exception
                        Toast.makeText(AccessTournament.this, "Error checking document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}