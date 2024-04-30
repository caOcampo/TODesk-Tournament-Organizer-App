package com.example.todeskapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.todeskapp.databinding.ActivityElimBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import toDesk.src.tournament.formats.DElim;
import toDesk.src.tournament.data.PlayerEntry;

import java.util.ArrayList;



public class ElimActivity extends AppCompatActivity {

    private ActivityElimBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    int initial;

    private String accessCode;
    private ExecutorService executorService;  // Executor service to run tasks in the background

    ArrayList<PlayerEntry> playerList;
    DElim DElimBracket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityElimBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        /*
        Intended implementation:
        - Receive the access code to the database of players
        - Instantiate DElim class with the list of players passed to upperBracket
        - Create a scrolling view containing brackets of ELimMatch
            - each DElim.Match would correspond to an ELimMatch layout

         */

        accessCode = getIntent().getStringExtra("ACCESS_CODE");




        /*
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });*/
        setContentView(R.layout.activity_elim);
    }

    private void retrievePlayerListAndCreateBracket(String accessCode) {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        playerList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            // Convert each document to a PlayerEntry object
                            PlayerEntry player = document.toObject(PlayerEntry.class);
                            playerList.add(player);
                        }
                        // Once the list is retrieved, create the upper bracket
                        DElimBracket.createUpperBracket(playerList);

                    } else {
                        // Handle errors
                    }
                });
    }

}