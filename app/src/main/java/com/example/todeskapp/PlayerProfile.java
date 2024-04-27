package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.todeskapp.databinding.PlayerProfileBinding;

/**
 *
 * This class provides display and functionality of the player_profile layout to the user.
 * Here, the user can configure the player profile to add them to the tournament. This allows the
 * user to enter details about the username, organization of the user, and rank of the user.
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */
public class PlayerProfile extends AppCompatActivity{

    /**
     * Binding the configure_tournament.xml file to this code.
     */
    private PlayerProfileBinding binding;
    /**
     * Initializing Firestore database.
     */
    private FirebaseFirestore db;
    /**
     * Holds the access code variable for knowing what document to access.
     */
    private String accessCode;

    /**
     * Sets the GUI to the player_profile.xml. Retrieves database information.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //sets display
        super.onCreate(savedInstanceState);
        binding = PlayerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize database, get access code
        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        //adds functionality to the add button
        binding.add.setOnClickListener(v -> addPlayer());
    }


    /**
     * Adds the user profile details to the database.
     *
     * @return No return value.
     *
     */
    private void addPlayer() {

        //get values from the user input
        String username = binding.inputUsername.getText().toString().trim();
        String organization = binding.inputOrganization.getText().toString().trim().isEmpty() ? "" : binding.inputOrganization.getText().toString().trim();
        String rank = binding.inputRank.getText().toString().trim().isEmpty() ? "" : binding.inputRank.getText().toString().trim();

        //checks if it is empty
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if(organization.isEmpty()){
            organization = "";
        }
        if(rank.isEmpty()) {
            rank = "";
        }

        //gets player details and adds it to the database
        Player player = new Player(username, organization, rank, 0, 0);

        db.collection("AccessCodes")
                .document(accessCode)
                .collection("PlayerList")
                .document(username)
                .set(player)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PlayerProfile.this, "Player added successfully", Toast.LENGTH_SHORT).show();
                    deleteInitialDoc();
                })
                .addOnFailureListener(e -> Toast.makeText(PlayerProfile.this, "Failed to add player: " + e.toString(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Remove the document initialized when PlayerList collection was created.
     *
     * @return No return value.
     *
     */
    private void deleteInitialDoc() {

        //get the initial document from the database
        db.collection("AccessCodes")
                .document(accessCode)
                .collection("PlayerList")
                .document("InitialDoc")
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Initial document removed successfully", Toast.LENGTH_SHORT).show();

                    //after delete redirect
                    navigateToAddPlayer();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove initial document: " + e.toString(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Redirect back to the AddPlayer page
     *
     * @return No return value.
     *
     */
    private void navigateToAddPlayer() {
        Intent intent = new Intent(PlayerProfile.this, AddPlayer.class);
        intent.putExtra("ACCESS_CODE", accessCode);  // Ensure accessCode is not null
        startActivity(intent);
    }

    /**
     * Class to hold details about the player
     *
     */
    public static class Player {
        private String username;
        private String organization;
        private String rank;
        private int win;
        private int loss;

        // Constructor
        public Player(String username, String organization, String rank, int win, int loss) {
            this.username = username;
            this.organization = organization;
            this.rank = rank;
            this.win = win;
            this.loss = loss;
        }

        // Getter methods
        public String getUsername() {
            return username;
        }

        public String getOrganization() {
            return organization;
        }

        public String getRank() {
            return rank;
        }

        public void incrementWins() {
            win++;
        }

        public void incrementLosses() {
            loss++;
        }

        public int getWins() {
            return win;
        }

        public int getLosses() {
            return loss;
        }
    }
}

