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

/**
 * This class provides display and functionality of the main_menu.xml layout to the user.
 * This is the first page that is available to the user. It allows the user to pick to between
 * creating a new tournament or just view an existing one
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */

public class MainMenu extends AppCompatActivity {

    /**
     * Binding the main_menu.xml file to this code.
     */
    private MainMenuBinding binding;
    /**
     * Initializing Firestore database.
     */
    private FirebaseFirestore db;

    /**
     * Sets the GUI to the main_menu.xml. Retrieves database information.
     * Adds functionality to create new tournament button and view existing tournament button.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //sets display
        super.onCreate(savedInstanceState);
        binding = MainMenuBinding.inflate(getLayoutInflater()); // Initialize the binding
        setContentView(binding.getRoot());

        //initialize Firebase database and authentication
        db = FirebaseFirestore.getInstance();

        //redirects to creating a new tournament
        binding.CNTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //randomly generates an access code
                String accessCode = generateRandomCode(5);
                createFirestoreDocumentAndRedirect(accessCode);

            }
        });

        //redirects to viewing an existing tournament
        binding.VETButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, AccessTournament.class);
            startActivity(intent);
        });

    }

    /**
     * Generates a random access code for this tournament.
     *
     * @param codeLength the length of the code. It should be 5 digits.
     * @return the generated access code
     *
     * */
    private String generateRandomCode(int codeLength) {

        //access code consists of only capital letters and numbers
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();

        //randomize
        Random random = new Random();

        //generating the code
        while (result.length() < codeLength) {
            int index = (int) (random.nextFloat() * characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    /**
     * Creates a document to hold the tournament information in the database. The title of the
     * document is the access code that was generated.
     *
     * @param documentName the name of the document that is the access code
     *
     * */
    private void createFirestoreDocumentAndRedirect(String documentName) {

        //Initialize a field in the document with a timestamp of when the document was created
        Map<String, Object> docData = new HashMap<>();
        docData.put("timestamp", System.currentTimeMillis());

        db.collection("AccessCodes").document(documentName)
                .set(docData)
                .addOnSuccessListener(aVoid -> {

                    //redirects to account choice options
                    redirectToAccountCreation(documentName);
                })
                .addOnFailureListener(e -> {

                    System.out.println("Error writing document: " + e);
                });
    }


    /**
     * Redirect to Account Choice and passing access code
     *
     * @param accessCode the access code
     *
     * */
    private void redirectToAccountCreation(String accessCode) {
        Intent intent = new Intent(MainMenu.this, AccountChoice.class);
        intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
        startActivity(intent);
    }



}
