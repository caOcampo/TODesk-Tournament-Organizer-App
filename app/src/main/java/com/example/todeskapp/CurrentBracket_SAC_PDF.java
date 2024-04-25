
package com.example.todeskapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.AddPlayerBinding;
import com.example.todeskapp.databinding.CurrentBracketSacPdfBinding;
import com.example.todeskapp.databinding.PlayerDisplayBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CurrentBracket_SAC_PDF extends AppCompatActivity {

    private CurrentBracketSacPdfBinding binding;
    private FirebaseFirestore db;
    private String accessCode;

    private LinearLayout bracketContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CurrentBracketSacPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        bracketContainer = findViewById(R.id.bracket_container);

        readAccessCodeFromFirebase(bracketContainer);
    }

    // THE TEMPLATE TO READ WHICH TOURNAMENT BRACKET IS BEING USED FROM ACCESS CODE
    /*private void readAccessCodeFromFirebase(LinearLayout bracketContainer) {
        db.collection("AccessCodes")
                .document(accessCode) // REPLACE WITH ACTUAL THING
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Check if the AccessCode contains keywords
                            String accessCode = document.getString("accessCode");
                            if (accessCode != null) {
                                if (accessCode.contains("Round Robin")) {
                                    displayRoundRobinPre(bracketContainer);
                                } else if (accessCode.contains("Double Elimination")) {
                                    displayElimMatch(bracketContainer);
                                } else if (accessCode.contains("Swiss Stage")) {
                                    displaySwissStage(bracketContainer);
                                }
                            }
                        }
                    }
                });
    }*/

    private void readAccessCodeFromFirebase(LinearLayout bracketContainer) {
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
                                        displaySwissStage(bracketContainer);
                                        break;
                                    case 1:
                                        displayRoundRobinPre(bracketContainer);
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

    private void displayRoundRobinPre(LinearLayout bracketContainer) {
        // Inflate the layout "round_robin_pre" and add it to the ScrollView
        LayoutInflater inflater = LayoutInflater.from(this);
        View roundRobinPreView = inflater.inflate(R.layout.round_robin_pre, bracketContainer, false);
        bracketContainer.addView(roundRobinPreView);
    }


    private void displaySwissStage(LinearLayout bracketContainer) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View swissStagePreview = inflater.inflate(R.layout.swiss_pool_display_8p, bracketContainer, false);
        bracketContainer.addView(swissStagePreview);
    }

    private void displayElimMatch(LinearLayout scrollView) {


    }
}

