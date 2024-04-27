

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
/**
 *
 * This class provides display and functionality of the add_player.xml layout to the user.
 * Here, the user can view the current player list and add or delete players from the tournament.
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */
public class AddPlayer extends AppCompatActivity {

    /**
     * Binding the add_player.xml file to this code.
     */
    private AddPlayerBinding binding;
    /**
     * Initializing Firestore database.
     */
    private FirebaseFirestore db;
    /**
     * Holds the access code variable for knowing what document to access.
     */
    private String accessCode;

    /**
     * Sets the GUI to the add_player.xml. Retrieves database information.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets display
        super.onCreate(savedInstanceState);
        binding = AddPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize database, get access code
        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        //displays player list
        fetchAndDisplayPlayers();

        //back button to ConfigureTournament
        backButton();

        //add players to the list
        addPlayerButton();
    }

    /**
     * Gets the list of players currently in the tournament and displays it.
     *
     * @return No return value.
     *
     */
    private void fetchAndDisplayPlayers() {
        //test to see if can retrieve access code document
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()

                //if can access
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        //for each document, get the name of it
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            //displays the player list
                            addPlayerToView(document.getId());
                        }
                    } else {
                        Toast.makeText(this, "Error fetching players: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Displays the player list onto the GUI.
     *
     * @param playerName the name of the document which is the player name
     * @return No return value.
     *
     */
    private void addPlayerToView(String playerName) {

        //displays the button
        PlayerDisplayBinding itemBinding = PlayerDisplayBinding.inflate(LayoutInflater.from(this), binding.playersContainer, false);

        //set the text of the button to a player name
        itemBinding.playerName.setText(playerName);

        //add a delete button that deletes the player from the list
        itemBinding.deleteButton.setOnClickListener(v -> deletePlayer(playerName));

        binding.playersContainer.addView(itemBinding.getRoot());
    }

    /**
     * Deletes a player from the PlayerList
     *
     * @param playerName the name of the document which is the player name
     * @return No return value.
     *
     */
    private void deletePlayer(String playerName) {

        //test to see if can retrieve access code document
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList").document(playerName)
                .delete()

                //if can access
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Player deleted successfully", Toast.LENGTH_SHORT).show();

                    //reset the view of the PlayerList by removing it
                    binding.playersContainer.removeAllViews();

                    //show the PlayerList again
                    fetchAndDisplayPlayers();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting player: " + e.toString(), Toast.LENGTH_SHORT).show());
    }


    /**
     * Add a player to the PlayerList
     *
     * @return No return value.
     *
     */
    private void addPlayerButton(){

        //add a player by clicking on a button
        binding.addPlayer.setOnClickListener(v -> {

            //test to see if can retrieve access code document
            db.collection("AccessCodes").document(accessCode)
                    .collection("PlayerList")
                    .get()

                    //if can access
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            //count the number of players by counting the number of documents
                            int playerCount = task.getResult().size();

                            //make sure the number of players do not exceed 16
                            if (playerCount >= 16) {
                                Toast.makeText(this, "There can only be a maximum of 16 players.", Toast.LENGTH_SHORT).show();
                            } else {

                                //redirect to configuring the player profile
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

    /**
     * Allows the user to go back to the Configure Tournament screen in order to add tournament
     * details.
     *
     * @return No return value.
     *
     */
    private void backButton() {
        binding.backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddPlayer.this, ConfigureTournament.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);
        });

    }
}

