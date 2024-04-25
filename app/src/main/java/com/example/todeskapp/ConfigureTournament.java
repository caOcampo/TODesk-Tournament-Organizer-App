package com.example.todeskapp;

import java.util.*;

import android.content.Intent;

import android.os.Bundle;

import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.ConfigureTournmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;



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

        binding.create.setOnClickListener(v -> {

            accessCode = getIntent().getStringExtra("ACCESS_CODE");

            bracketStyleRedirect(accessCode);

        });
    }

    private void bracketStyleRedirect(String accessCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the specific document reference
        db.collection("AccessCodes").document(accessCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {

                            Long bracketStyleLong = document.getLong("BracketStyle");


                            if (bracketStyleLong != null) {
                                int bracketStyle = bracketStyleLong.intValue();
                                switch (bracketStyle) {
                                    case 0:

                                        performActionBasedOnBracketStyle();
                                        break;

                                    default:
                                        Toast.makeText(this, "Unhandled bracket style: " + bracketStyle, Toast.LENGTH_SHORT).show();
                                }
                            }

                            else {
                                Toast.makeText(this, "BracketStyle field is missing.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else {
                            Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else {
                        Toast.makeText(this, "Failed to fetch document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performActionBasedOnBracketStyle() {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int playerCount = task.getResult().size();

                            if (playerCount == 8 || playerCount == 16) {

                                Intent intent = new Intent(ConfigureTournament.this, Swiss.class);
                                intent.putExtra("ACCESS_CODE", accessCode);
                                startActivity(intent);

                            } else {

                                Toast.makeText(ConfigureTournament.this, "Swiss Pool MUST HAVE 8 or 16 players", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ConfigureTournament.this, "Failed to fetch players: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}


