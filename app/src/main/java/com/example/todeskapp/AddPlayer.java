

package com.example.todeskapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.AddPlayerBinding;
import com.example.todeskapp.databinding.PlayerDisplayBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AddPlayer extends AppCompatActivity {

    private AddPlayerBinding binding;
    private FirebaseFirestore db;

    private String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        fetchAndDisplayPlayers();


        binding.backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddPlayer.this, ConfigureTournament.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);
        });


        addPlayerButton();
    }

    private void fetchAndDisplayPlayers() {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            /*get the player names*/
                            addPlayerToView(document.getId());
                        }
                    } else {
                        Toast.makeText(this, "Error fetching players: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPlayerToView(String playerName) {

        PlayerDisplayBinding itemBinding = PlayerDisplayBinding.inflate(LayoutInflater.from(this), binding.playersContainer, false);

        itemBinding.playerName.setText(playerName);
        itemBinding.deleteButton.setOnClickListener(v -> deletePlayer(playerName));

        binding.playersContainer.addView(itemBinding.getRoot());
    }

    private void deletePlayer(String playerName) {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList").document(playerName)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Player deleted successfully", Toast.LENGTH_SHORT).show();
                    binding.playersContainer.removeAllViews();
                    fetchAndDisplayPlayers();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting player: " + e.toString(), Toast.LENGTH_SHORT).show());
    }

    private void addPlayerButton(){
        binding.addPlayer.setOnClickListener(v -> {

            db.collection("AccessCodes").document(accessCode)
                    .collection("PlayerList")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int playerCount = task.getResult().size();
                            if (playerCount >= 16) {
                                Toast.makeText(this, "There can only be a maximum of 16 players.", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(AddPlayer.this, PlayerProfile.class);
                                intent.putExtra("ACCESS_CODE", accessCode);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(this, "Error checking player count: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}

