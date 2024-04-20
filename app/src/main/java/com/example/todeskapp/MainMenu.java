package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.MainMenuBinding;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainMenu extends AppCompatActivity {

    private MainMenuBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainMenuBinding.inflate(getLayoutInflater()); // Initialize the binding
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        /*binding.CNTButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, AccountChoice.class);
            startActivity(intent);
        });*/

        binding.CNTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String randomCode = generateRandomCode(5);
                createFirestoreDocumentAndRedirect(randomCode);
            }
        });


        binding.VETButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, AccessTournament.class);
            startActivity(intent);
        });

    }

    private String generateRandomCode(int codeLength) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        while (result.length() < codeLength) {
            int index = (int) (random.nextFloat() * characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    private void createFirestoreDocumentAndRedirect(String documentName) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("timestamp", System.currentTimeMillis());

        db.collection("AccessCodes").document(documentName)
                .set(docData)
                .addOnSuccessListener(aVoid -> {
                    // Document written successfully, now redirect to another activity
                    redirectToAnotherActivity(documentName);;
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    System.out.println("Error writing document: " + e);
                });
    }

    private void redirectToAnotherActivity(String accessCode) {
        Intent intent = new Intent(this, AccountChoice.class);
        intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
        startActivity(intent);
    }



}
