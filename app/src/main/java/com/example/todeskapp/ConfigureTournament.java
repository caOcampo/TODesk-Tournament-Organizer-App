package com.example.todeskapp;

import android.content.Intent;
import android.graphics.Bitmap;
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
        binding.playerSettings.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(ConfigureTournament.this, AddPlayer.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });

        /* Setup redirection for the tournament settings */
        binding.tournamentSettings.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(ConfigureTournament.this, TournamentSettings.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });

        binding.create.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // handleCreateTournament();
            }
        });
    }



}


