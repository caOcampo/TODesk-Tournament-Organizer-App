package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.security.ConfirmationCallback;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.ConfigureTournmentBinding;

public class ConfigureTournament extends AppCompatActivity {

    private ConfigureTournmentBinding binding;
    private String accessCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ConfigureTournmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        accessCode = getIntent().getStringExtra("ACCESS_CODE");


        /* Setup redirection for the player settings */
        binding.playerSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddPlayer(accessCode);
            }
        });

        /* Setup redirection for the tournament settings */
        binding.tournamentSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTournamentSettings(accessCode);
            }
        });

        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handleCreateTournament();
            }
        });
    }

    /* Redirect to AddPlayer activity */
    private void navigateToAddPlayer(String accessCode) {
        Intent intent = new Intent(ConfigureTournament.this, AddPlayer.class);
        intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
        startActivity(intent);
    }

    /* Redirect to TournamentSettings activity */
    private void navigateToTournamentSettings(String accessCode) {
        Intent intent = new Intent(ConfigureTournament.this, TournamentSettings.class);
        intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
        startActivity(intent);
    }

    // Optionally, handle create button click if needed
    // private void handleCreateTournament() {
    //     // Implementation here
    // }
}


