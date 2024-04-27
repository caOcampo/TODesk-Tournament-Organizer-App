package com.example.todeskapp;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PlayerPlacements extends Activity {

    private DatabaseReference mDatabase;
    private LinearLayout playerListLayout;

    String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessCode = getIntent().getStringExtra("ACCESS_CODE");
        //for testing
        accessCode = "ABCED";

        setContentView(R.layout.player_placements);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PlayerList");

        // Get reference to LinearLayout inside ScrollView
        playerListLayout = findViewById(R.id.playerListLayout);

        // Load player list from Firebase
        loadPlayerList();
    }

    private void loadPlayerList() {
        // Add listener to fetch data from Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing player items
                playerListLayout.removeAllViews();

                // Iterate through each player in the dataSnapshot
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    // Get player details
                    String userName = playerSnapshot.child("UserName").getValue(String.class);
                    long placement = playerSnapshot.child("Placement").getValue(Long.class);

                    // Create TextView for each player
                    TextView playerTextView = new TextView(PlayerPlacements.this);
                    playerTextView.setText(userName + " - Placement: " + placement);

                    // Add OnClickListener to handle placement editing
                    playerTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Implement logic for editing placement
                            // This could involve reordering players in the list
                            // and updating their placement values in Firebase
                            Toast.makeText(PlayerPlacements.this, "Clicked on: " + userName, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Add TextView to the LinearLayout
                    playerListLayout.addView(playerTextView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(PlayerPlacements.this, "Failed to load player list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}