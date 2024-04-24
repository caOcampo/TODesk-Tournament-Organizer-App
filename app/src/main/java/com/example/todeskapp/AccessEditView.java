package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.todeskapp.databinding.AccessEditViewBinding;

public class AccessEditView extends AppCompatActivity {

    private AccessEditViewBinding binding;
    private FirebaseFirestore db;
    private String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AccessEditViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        getTournamentName();

        binding.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccessEditView.this, OrganizerLogin.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);
        });



    }

    private void getTournamentName() {
        db.collection("AccessCodes").document(accessCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tournamentName = documentSnapshot.getString("TournamentName");
                        binding.tournamentNameView.setText(tournamentName);
                    } else {
                        Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error getting document: " + e.toString(), Toast.LENGTH_SHORT).show();
                });
    }



}