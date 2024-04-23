package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.PlayerProfileBinding;

public class PlayerProfile extends AppCompatActivity{

    private PlayerProfileBinding binding;
    private String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PlayerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        /* Setup redirection from PlayerProfile to AddPlayer */
        binding.add.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(PlayerProfile.this, AddPlayer.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });
    }
}

