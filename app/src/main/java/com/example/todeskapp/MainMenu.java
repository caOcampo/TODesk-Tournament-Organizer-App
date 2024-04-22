package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        /* redirection through buttons*/
        binding.CNTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessCode = generateRandomCode(5);
                createFirestoreDocumentAndRedirect(accessCode);

            }
        });


        binding.VETButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, AccessTournament.class);
            startActivity(intent);
        });

    }

    /* Once Create New Tournament button is selected, generates an ACCESS code*/
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

    /* Creates an [AccessCode] Document in the database under the collection for Access Codes" */
    private void createFirestoreDocumentAndRedirect(String documentName) {
        Map<String, Object> docData = new HashMap<>();
        docData.put("timestamp", System.currentTimeMillis());

        db.collection("AccessCodes").document(documentName)
                .set(docData)
                .addOnSuccessListener(aVoid -> {
                    /*Creates a document with the accesscode*/
                    redirectToAnotherActivity(documentName);
                })
                .addOnFailureListener(e -> {
                    /*data base error*/
                    System.out.println("Error writing document: " + e);
                });
    }


    /* Makes sure the Access code gets passed along for correct database locating*/
    private void redirectToAnotherActivity(String accessCode) {
        Intent intent = new Intent(MainMenu.this, AccountChoice.class);
        intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
        startActivity(intent);
    }



}
