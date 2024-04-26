package com.example.todeskapp;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.SwissPoolDisplay8pBinding;
import com.example.todeskapp.databinding.SwissPoolDisplay16pBinding;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Swiss extends AppCompatActivity {

    private FirebaseFirestore db;
    private SwissPoolDisplay8pBinding binding;
    private SwissPoolDisplay16pBinding binding16;

    private String accessCode;

    private List<String> playerNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SwissPoolDisplay8pBinding.inflate(getLayoutInflater());

        binding16 = SwissPoolDisplay16pBinding.inflate(getLayoutInflater());

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

                        if(playerNames.size() == 8){
                            setContentView(binding.getRoot());
                        }
                        else{
                            setContentView(binding16.getRoot());
                        }

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
                    String matchText = match.getPlayer1() + "\n" + match.getPlayer2();
                    button.setText(matchText);

                    // Set onClickListener to show a selection dialog
                    button.setOnClickListener(v -> {

                        CharSequence[] items = {match.getPlayer1(), match.getPlayer2()};
                        AlertDialog.Builder builder = new AlertDialog.Builder(Swiss.this);
                        builder.setTitle("Select the winner:");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // `which` will be 0 for player1 and 1 for player2
                                String winner = items[which].toString();
                                String loser = items[1 - which].toString(); // Gets the other player

                                updatePlayerStats(winner, loser, accessCode);
                            }
                        });
                        builder.show();
                    });
                }
                else {
                    Toast.makeText(Swiss.this, "Button not found for: " + key, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePlayerStats(String winner, String loser, String accessCode) {
        // Increment win for the winner
        db.collection("AccessCodes").document(accessCode).collection("PlayerList").document(winner)
                .update("win", FieldValue.increment(1));

        // Increment loss for the loser
        db.collection("AccessCodes").document(accessCode).collection("PlayerList").document(loser)
                .update("loss", FieldValue.increment(1));

        // Optionally update UI or navigate to another activity after updating stats
    }

    private void navigateToBracketDisplay() {
        Intent intent = new Intent(Swiss.this, CurrentBracket_SAC_PDF.class);
        intent.putExtra("ACCESS_CODE", accessCode);
        intent.putExtra("NUMBER_OF_PLAYERS", playerNames.size());
        startActivity(intent);
    }



}
