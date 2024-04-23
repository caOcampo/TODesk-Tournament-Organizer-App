package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.AddPlayerBinding;


public class AddPlayer extends AppCompatActivity{


    private AddPlayerBinding binding;
    private String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        accessCode = getIntent().getStringExtra("ACCESS_CODE");


        /* Setup redirection for the player information */
        binding.addPlayer.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(AddPlayer.this, PlayerProfile.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });


        binding.backButton.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(AddPlayer.this, ConfigureTournament.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });


    }
}
