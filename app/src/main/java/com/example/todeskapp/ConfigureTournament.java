package com.example.todeskapp;

import java.util.*;

import android.content.Intent;

import android.os.Bundle;

import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.ConfigureTournmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

/**
 *
 * This class provides display and functionality of the configure_tournament layout to the user.
 * Here, the user can configure the tournament. This directs the user to adding players or
 * changing the tournament settings.
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */

public class ConfigureTournament extends AppCompatActivity {

    /**
     * Binding the configure_tournament.xml file to this code.
     */
    private ConfigureTournmentBinding binding;
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
        binding = ConfigureTournmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize database, get access code
        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        //initialize database for PlayerList Collection
        initializePlayerListCollection();

        //making the buttons functional
        ButtonListeners();
    }

    /**
     * Sets the GUI to the add_player.xml. Retrieves database information.
     *
     * @return No return value.
     *
     */
    private void initializePlayerListCollection() {

        //initialize a collection called PlayerList
        CollectionReference playerListRef = db.collection("AccessCodes").document(accessCode).collection("PlayerList");

        playerListRef.limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                if (task.getResult().isEmpty()) {

                    //the Collection is initialized with a document
                    HashMap<String, Object> initDoc = new HashMap<>();
                    initDoc.put("Init", true);

                    playerListRef.document("InitialDoc").set(initDoc)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "PlayerList collection initialized successfully.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error initializing PlayerList collection: " + e.getMessage(), Toast.LENGTH_LONG).show());
                } else {
                    // Documents already exist, no need to add the initial document
                    Toast.makeText(getApplicationContext(), "PlayerList already contains data.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Failed to retrieve documents
                Toast.makeText(getApplicationContext(), "Failed to check PlayerList collection: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Adds functionality for buttons to add players to the tournament and change settings of the tournament.
     *
     * @return No return value.
     *
     */
    private void ButtonListeners() {

        //Setup redirection for the adding players
        binding.playerSettings.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(ConfigureTournament.this, AddPlayer.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });

        // Setup redirection for the tournament settings
        binding.tournamentSettings.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(ConfigureTournament.this, TournamentSettings.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });

        // Setup redirection for tournament display
        binding.create.setOnClickListener(v -> {

            accessCode = getIntent().getStringExtra("ACCESS_CODE");

            //redirection depends on the bracket format
            bracketStyleRedirect(accessCode);

        });
    }

    /**
     * Figures out which bracketStyle is chosen and then redirects the user to the corresponding
     * bracket display
     *
     * @param accessCode access code of the tournament
     * @return No return value.
     *
     */
    private void bracketStyleRedirect(String accessCode) {

        //test to see if can retrieve access code document
        db.collection("AccessCodes").document(accessCode)
                .get()

                //if can access
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {

                            //gets the bracketStyle field value which is an integer that corresponds to each format

                            Long bracketStyleLong = document.getLong("BracketStyle");

                            if (bracketStyleLong != null) {
                                int bracketStyle = bracketStyleLong.intValue();
                                switch (bracketStyle) {

                                    // swiss pool
                                    case 0:
                                        swissPlayerValidation();
                                        break;

                                    // round robin
                                    case 1:
                                        performActionBasedOnBracketStyle1();
                                        break;
                                    case 2:
                                        performActionBasedOnBracketStyle1();
                                        break;


                                    default:
                                        Toast.makeText(this, "Unhandled bracket style: " + bracketStyle, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "BracketStyle field is missing.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Before redirecting to Swiss Pool bracket configuration, it checks the number of player again.
     * Swiss Pool can only deal with 8 players or 16 players.
     *
     * @return No return value.
     *
     */
    private void swissPlayerValidation() {
        //test to see if can retrieve access code document
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()

                //if can access
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int playerCount = task.getResult().size();

                            //only proceed to redirect if the number of players is 8 or 16
                            if (playerCount == 8 || playerCount == 16) {

                                Intent intent = new Intent(ConfigureTournament.this, Swiss.class);
                                intent.putExtra("ACCESS_CODE", accessCode);
                                startActivity(intent);

                            } else {

                                Toast.makeText(ConfigureTournament.this, "Swiss Pool MUST HAVE 8 or 16 players", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ConfigureTournament.this, "Failed to fetch players: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void performActionBasedOnBracketStyle1() {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ConfigureTournament.this, CurrentBracket_SAC_PDF.class);
                            intent.putExtra("ACCESS_CODE", accessCode);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ConfigureTournament.this, "Failed to fetch players: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }


                });

    }

    private void startRoundRobinPreTestingActivity() {
        Intent intent = new Intent(ConfigureTournament.this, RoundRobinPreTesting.class);
        intent.putExtra("ACCESS_CODE", accessCode);
        startActivity(intent);
    }
}