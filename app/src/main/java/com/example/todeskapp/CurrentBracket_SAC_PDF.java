
package com.example.todeskapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CurrentBracketSacPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        String accessCode = getIntent().getStringExtra("ACCESS_CODE");

        // Find the ScrollView in the layout
        ScrollView scrollView = findViewById(R.id.current_bracket);

        // Read the AccessCode from Firebase
        readAccessCodeFromFirebase(scrollView);
    }

    // THE TEMPLATE TO READ WHICH TOURNAMENT BRACKET IS BEING USED FROM ACCESS CODE
    private void readAccessCodeFromFirebase(ScrollView scrollView) {
        db.collection("AccessCodes")
                .document("YOUR_ACCESS_CODE_DOCUMENT_ID") // REPLACE WITH ACTUAL THING
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Check if the AccessCode contains keywords
                            String accessCode = document.getString("accessCode");
                            if (accessCode != null) {
                                if (accessCode.contains("Round Robin")) {
                                    displayRoundRobinPre(scrollView);
                                } else if (accessCode.contains("Double Elimination")) {
                                    displayElimMatch(scrollView);
                                } else if (accessCode.contains("Swiss Stage")) {
                                    displaySwissStage(scrollView);
                                }
                            }
                        }
                    }
                });
    }

    private void displayRoundRobinPre(ScrollView scrollView) {
        // Inflate the layout "round_robin_pre" and add it to the ScrollView
        LayoutInflater inflater = LayoutInflater.from(this);
        View roundRobinPreView = inflater.inflate(R.layout.round_robin_pre, scrollView, false);
        scrollView.addView(roundRobinPreView);
    }


    private void displaySwissStage(ScrollView scrollView) {
    }

    private void displayElimMatch(ScrollView scrollView) {
    }
}

