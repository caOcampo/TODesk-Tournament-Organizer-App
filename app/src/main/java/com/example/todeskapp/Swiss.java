package com.example.todeskapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todeskapp.databinding.SwissPoolDisplay8pBinding;
import com.example.todeskapp.databinding.SwissPoolDisplay16pBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


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

        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();


        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        if (accessCode != null) {
            swissBracketStructure(accessCode);
            /*initializeSwissStage(accessCode, 1);*/
        } else {
            Toast.makeText(this, "Access code not found.", Toast.LENGTH_SHORT).show();
        }

    }


    public void swissBracketStructure(String accessCode) {

        db.collection("AccessCodes").document(accessCode).collection("PlayerList")
                .get().addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult().size() == 8) {

                        int[] levels = {0, 1, 2, 3};

                        for (int i : levels) {
                            for (int j : levels) {

                                if (i == 3 && j == 3) continue;

                                String collectionName = "" + i + j;

                                int docCount = (i == 0 && j == 0) ? 4 : ((i + j == 1) ? 2 : 1);

                                for (int docIndex = 1; docIndex <= docCount; docIndex++) {
                                    Map<String, Object> docData = new HashMap<>();
                                    docData.put("player1", "");
                                    docData.put("player2", "");
                                    docData.put("winRedirect", (i + 1) + "" + j);
                                    docData.put("lossRedirect", i + "" + (j + 1));

                                    db.collection("AccessCodes").document(accessCode)
                                            .collection(collectionName).document(collectionName + "_" + docIndex)
                                            .set(docData);
                                }
                            }
                        }
                    } else {
                        System.out.println("Error: There are not exactly 8 players in the tournament.");
                    }
                });

        intializeMatches(accessCode);
    }

    public void intializeMatches(String accessCode) {
        db.collection("AccessCodes").document(accessCode).collection("PlayerList")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot.size() == 8) {
                            List<String> usernames = snapshot.getDocuments().stream()
                                    .map(document -> document.getId())
                                    .collect(Collectors.toList());
                            Collections.shuffle(usernames);

                            AtomicInteger updatesCount = new AtomicInteger();
                            for (int i = 0; i < 4; i++) {
                                Map<String, Object> docData = new HashMap<>();
                                docData.put("player1", usernames.get(i * 2));
                                docData.put("player2", usernames.get(i * 2 + 1));
                                docData.put("winRedirect", "10");
                                docData.put("lossRedirect", "01");

                                db.collection("AccessCodes").document(accessCode)
                                        .collection("00").document("00_" + (i + 1))
                                        .set(docData).addOnSuccessListener(aVoid -> {
                                            if (updatesCount.incrementAndGet() == 4) {
                                                updateButtons(accessCode); // Call updateButtons only after all documents are updated
                                            }
                                        });
                            }
                        } else {
                            System.out.println("There are not exactly 8 players in the tournament.");
                        }
                    } else {
                        System.out.println("Error fetching player list: " + task.getException());
                    }
                });
    }

    private void updateButtons(String accessCode) {
        String[] collectionNames = {"00", "10", "01", "20", "11", "02", "30", "21", "12", "03", "31", "22", "13", "32", "23"};

        AtomicInteger totalUpdates = new AtomicInteger();
        AtomicInteger updatesCompleted = new AtomicInteger();

        for (String collectionName : collectionNames) {
            int docCount;

            if (collectionName.equals("00")) {
                docCount = 4;
            } else if (collectionName.equals("10") || collectionName.equals("01")) {
                docCount = 2;
            } else {
                docCount = 1;
            }

            totalUpdates.addAndGet(docCount);

            for (int i = 1; i <= docCount; i++) {
                final String docId = collectionName + "_" + i;
                db.collection("AccessCodes").document(accessCode).collection(collectionName).document(docId)
                        .get().addOnSuccessListener(documentSnapshot -> {
                            runOnUiThread(() -> {
                                String player1 = documentSnapshot.getString("player1");
                                String player2 = documentSnapshot.getString("player2");
                                player1 = player1.isEmpty() ? "<player>" : player1;
                                player2 = player2.isEmpty() ? "<player>" : player2;

                                String buttonId = "score" + docId;
                                int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
                                Button button = (Button) binding.getRoot().findViewById(resID);
                                if (button != null) {
                                    button.setText(player1 + "\n" + player2);
                                }
                                if (updatesCompleted.incrementAndGet() == totalUpdates.get()) {
                                    navigateToBracketDisplay();
                                }
                            });
                        }).addOnFailureListener(e -> {
                            System.out.println("Error fetching document: " + e.getMessage());
                        });
            }
        }
    }


    private void navigateToBracketDisplay() {
        Intent intent = new Intent(Swiss.this, CurrentBracket_SAC_PDF.class);
        intent.putExtra("ACCESS_CODE", accessCode);
        startActivity(intent);
    }


        /*public void initializeSwissStage(String accessCode, int stageNumber) {
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
                    String matchText = match.player1 + "\n" + match.player2;
                    button.setText(matchText);
                } else {
                    Toast.makeText(Swiss.this, "Button not found for: " + key, Toast.LENGTH_SHORT).show();
                }
            }
            navigateToBracketDisplay();
        });
    }*/


}