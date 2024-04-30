package com.example.todeskapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.firestore.Query;

import com.example.todeskapp.databinding.SwissPoolDisplay8pBinding;
import com.example.todeskapp.databinding.SwissPoolDisplay16pBinding;
import com.example.todeskapp.databinding.CurrentBracketSacPdfBinding;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class SwissEdit extends AppCompatActivity {

    private FirebaseFirestore db;
    private SwissPoolDisplay8pBinding binding8p;
    private SwissPoolDisplay16pBinding binding16;
    private CurrentBracketSacPdfBinding bindingDisplay;
    private String accessCode;

    private LinearLayout bracketContainer;

    private ProgressBar progressBar;

    private List<String> playerNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding8p = SwissPoolDisplay8pBinding.inflate(getLayoutInflater());
        binding16 = SwissPoolDisplay16pBinding.inflate(getLayoutInflater());
        bindingDisplay = CurrentBracketSacPdfBinding.inflate(getLayoutInflater());

        setContentView(bindingDisplay.getRoot());

        db = FirebaseFirestore.getInstance();


        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        bracketContainer = findViewById(R.id.bracket_container);

        progressBar = findViewById(R.id.progressBar);

        if (accessCode != null) {
            swissBracketStructure(accessCode);
            /*initializeSwissStage(accessCode, 1);*/
        } else {
            Toast.makeText(this, "Access code not found.", Toast.LENGTH_SHORT).show();
        }

        bindingDisplay.saveAsPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAsPdf(bracketContainer);
            }
        });

        bindingDisplay.homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SwissEdit.this, MainMenu.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);
        });

        bindingDisplay.SaveAccessCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SwissEdit.this, accessCode, Toast.LENGTH_SHORT).show();
            }
        });

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

        initializeMatches(accessCode);
    }

    public void initializeMatches(String accessCode) {
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
                                                updateButtons(accessCode);
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
                                Button button = (Button) binding8p.getRoot().findViewById(resID);
                                if (button != null) {
                                    button.setText(player1 + "\n" + player2);
                                    final String changePlayer1 = player1;
                                    final String changePlayer2 = player2;

                                    button.setOnClickListener(v -> showPlayerChoice(docId, changePlayer1, changePlayer2, documentSnapshot.getString("winRedirect")));
                                }
                                if (updatesCompleted.incrementAndGet() == totalUpdates.get()) {
                                    bracketDisplayer();

//                                    setContentView(binding8p.getRoot());
                                }
                            });
                        }).addOnFailureListener(e -> {
                            System.out.println("Error fetching document: " + e.getMessage());
                        });
            }
        }
    }

    private void showLoadingIndicator() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        progressBar.setVisibility(View.GONE);
    }

    private void showPlayerChoice(String docId, String player1, String player2, String winRedirect) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select a winner");

        String[] players = {player1, player2};

        builder.setItems(players, (dialog, which) -> {

            String selectedPlayer = players[which];
            movePlayerToNextRound(selectedPlayer, winRedirect);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void movePlayerToNextRound(String playerName, String winRedirect) {
        db.collection("AccessCodes").document(accessCode).collection(winRedirect)
                .orderBy("docIndex")
                .get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        boolean playerPlaced = false;

                        for (DocumentSnapshot document : task.getResult()) {

                            String player1 = document.getString("player1");
                            String player2 = document.getString("player2");

                            if (player1 == null || player1.isEmpty()) {
                                document.getReference().update("player1", playerName);
                                playerPlaced = true;
                                break;

                            } else if (player2 == null || player2.isEmpty()) {
                                document.getReference().update("player2", playerName);
                                playerPlaced = true;
                                break;
                            }
                        }
                    } else {
                        Toast.makeText(this, "Firestore Error: Failed to fetch documents for moving player.", Toast.LENGTH_SHORT).show();
                        //Log.e("Firestore Error", "Failed to fetch documents for moving player", task.getException());
                    }
                });

        bracketDisplayer();
    }

    private void saveAsPdf(LinearLayout bracketContainer) {
        //get access code document
        db.collection("AccessCodes").document(accessCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tournamentName = documentSnapshot.getString("TournamentName");

                        PdfDocument pdfDocument = new PdfDocument();
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bracketContainer.getChildAt(0).getWidth(),
                                bracketContainer.getChildAt(0).getHeight(), 1).create();
                        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                        bracketContainer.draw(page.getCanvas());
                        pdfDocument.finishPage(page);

                        // Using getExternalFilesDir for compatibility with scoped storage
                        File pdfFile = new File(getExternalFilesDir(null), tournamentName + ".pdf");

                        try {
                            FileOutputStream outputStream = new FileOutputStream(pdfFile);
                            pdfDocument.writeTo(outputStream);
                            outputStream.close();
                            pdfDocument.close();

                            // Include the path in the toast message
                            Toast.makeText(this, "PDF saved successfully at " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error getting document: " + e.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    public void bracketDisplayer(){
        showLoadingIndicator();
        hideLoadingIndicator();
        bracketContainer.removeAllViews();
        bracketContainer.addView(binding8p.getRoot());
    }




}