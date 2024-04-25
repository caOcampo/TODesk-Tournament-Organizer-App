package com.example.todeskapp;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.SwissPoolDisplay8pBinding;
import com.example.todeskapp.databinding.SwissPoolDisplay16pBinding;


import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Swiss extends AppCompatActivity {

    private FirebaseFirestore db;
    private SwissPoolDisplay8pBinding binding;

    private String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SwissPoolDisplay8pBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();


        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        if (accessCode != null) {
            initializeSwissStage(accessCode, 1);
        } else {
            Toast.makeText(this, "Access code not found.", Toast.LENGTH_SHORT).show();
        }

    }
    public void initializeSwissStage(String accessCode, int stageNumber) {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        List<String> playerNames = new ArrayList<>();

                        task.getResult().forEach(document -> {
                            playerNames.add(document.getId());
                        });


                        Collections.shuffle(playerNames);


                        int rounds = playerNames.size() / 2;



                        initializePlayerStats(accessCode, playerNames);
                        matchMaking(accessCode, playerNames, stageNumber);

                    } else {
                        System.err.println("Error fetching players: " + task.getException().getMessage());
                    }
                });
    }

    private void initializePlayerStats(String accessCode, List<String> playerNames) {
        for (String playerName : playerNames) {
            db.collection("AccessCodes")
                    .document(accessCode)
                    .collection("PlayerList")
                    .document(playerName)
                    .update("win", 0, "loss", 0)
                    .addOnSuccessListener(aVoid -> System.out.println("Player stats updated for: " + playerName))
                    .addOnFailureListener(e -> System.err.println("Error updating player stats: " + e.getMessage()));
        }
    }

    private void matchMaking(String accessCode, List<String> playerNames, int stageNumber) {
        int numberOfPlayers = playerNames.size();
        Map<String, Match> matchups = new HashMap<>();

        for (int i = 0; i < numberOfPlayers-1; i += 2) {

            String matchId = "zero_zero_match" + ((i / 2) + 1);
            String player1 = playerNames.get(i);
            String player2 = playerNames.get(i + 1);
            matchups.put(matchId, new Match(player1, player2));


            db.collection("AccessCodes")
                    .document(accessCode)
                    .collection("Matchups Stage: "+ stageNumber)
                    .add(new Match(player1, player2))
                    .addOnSuccessListener(documentReference -> System.out.println("Match created between " + player1 + " and " + player2))
                    .addOnFailureListener(e -> System.err.println("Failed to create match: " + e.getMessage()));



        }
        updateMatchupButtons(matchups);

    }

    class Match {
        private String player1;
        private String player2;

        Match(String player1, String player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        public String getPlayer1() {
            return player1;
        }

        public String getPlayer2() {
            return player2;
        }

        public void setPlayer1(String player1) {
            this.player1 = player1;
        }

        public void setPlayer2(String player2) {
            this.player2 = player2;
        }
    }

    public void updateMatchupButtons(Map<String, Match> matchups) {
        new Handler(Looper.getMainLooper()).post(() -> {
            for (Map.Entry<String, Match> entry : matchups.entrySet()) {

                String key = entry.getKey();
                Match match = entry.getValue();

                int buttonId = getResources().getIdentifier(key, "id", getPackageName());

                Button button = findViewById(buttonId);

                if (button != null) {
                    String matchText = match.player1 + "\n" + match.player2;
                    button.setText(matchText);
                } else {
                    Toast.makeText(Swiss.this, "Button not found for: " + key, Toast.LENGTH_SHORT).show();
                }
            }
            navigateToBracketDisplay();
        });
    }

    private void navigateToBracketDisplay() {
        Intent intent = new Intent(Swiss.this, CurrentBracket_SAC_PDF.class);
        intent.putExtra("ACCESS_CODE", accessCode);
        startActivity(intent);
    }
}
