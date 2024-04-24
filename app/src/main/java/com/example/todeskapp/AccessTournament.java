package com.example.todeskapp;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

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

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
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

                            validAccessCode = accessCode;
                            Toast.makeText(AccessTournament.this, "Access Code is valid!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AccessTournament.this, AccessEditView.class);
                            intent.putExtra("ACCESS_CODE", validAccessCode);
                            startActivity(intent);

                        } else {

                            Toast.makeText(AccessTournament.this, "Invalid Access Code.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(AccessTournament.this, "Error checking document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}