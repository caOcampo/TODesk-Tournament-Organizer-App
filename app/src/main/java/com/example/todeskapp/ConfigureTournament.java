package com.example.todeskapp;

import java.util.*;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.ConfigureTournmentBinding;
import com.google.firebase.firestore.FirebaseFirestore;


public class ConfigureTournament extends AppCompatActivity {

    private ConfigureTournmentBinding binding;
    private FirebaseFirestore db;
    private String accessCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ConfigureTournmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        initializePlayerListCollection();

        ButtonListeners();
    }

    private void initializePlayerListCollection() {

        HashMap<String, Object> initDoc = new HashMap<>();
        initDoc.put("Init", true);

        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList").document("InitialDoc")
                .set(initDoc)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("PlayerList collection initialized successfully.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error initializing PlayerList collection: " + e.getMessage());
                });
    }

    private void ButtonListeners(){
        /* Setup redirection for the player settings */
        binding.playerSettings.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(ConfigureTournament.this, AddPlayer.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });

        /* Setup redirection for the tournament settings */
        binding.tournamentSettings.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(ConfigureTournament.this, TournamentSettings.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });

        binding.create.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // handleCreateTournament();
            }
        });
    }



}


