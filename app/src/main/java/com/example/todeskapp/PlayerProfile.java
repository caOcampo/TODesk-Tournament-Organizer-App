package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.todeskapp.databinding.PlayerProfileBinding;

public class PlayerProfile extends AppCompatActivity{

    private PlayerProfileBinding binding;
    private FirebaseFirestore db;
    private String accessCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PlayerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        /* Setup redirection from PlayerProfile to AddPlayer */
        binding.add.setOnClickListener(v -> addPlayer());
    }



    private void addPlayer() {
        String username = binding.inputUsername.getText().toString().trim();
        String organization = binding.inputOrganization.getText().toString().trim().isEmpty() ? "" : binding.inputOrganization.getText().toString().trim();
        String rank = binding.inputRank.getText().toString().trim().isEmpty() ? "" : binding.inputRank.getText().toString().trim();

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

    private void deleteInitialDoc() {
        db.collection("AccessCodes")
                .document(accessCode)
                .collection("PlayerList")
                .document("InitialDoc")
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Initial document removed successfully", Toast.LENGTH_SHORT).show();
                    navigateToAddPlayer();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove initial document: " + e.toString(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToAddPlayer() {
        Intent intent = new Intent(PlayerProfile.this, AddPlayer.class);
        intent.putExtra("ACCESS_CODE", accessCode);  // Ensure accessCode is not null
        startActivity(intent);
    }
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

        public int getWin() {
            return win;
        }

        public int getLoss() {
            return loss;
        }
    }
}

