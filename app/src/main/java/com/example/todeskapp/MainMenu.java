package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.MainMenuBinding;

public class MainMenu extends AppCompatActivity {

    private MainMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainMenuBinding.inflate(getLayoutInflater()); // Initialize the binding
        setContentView(binding.getRoot());

        binding.CNTButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, AccountChoice.class);
            startActivity(intent);
        });

        binding.VETButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenu.this, AccessTournament.class);
            startActivity(intent);
        });



    }



}
