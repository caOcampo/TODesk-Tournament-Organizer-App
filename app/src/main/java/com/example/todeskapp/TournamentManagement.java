package com.example.todeskapp;

import androidx.appcompat.app.AppCompatActivity;


import com.example.todeskapp.databinding.TournamentManagementBinding;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
public class TournamentManagement extends AppCompatActivity {

    private TournamentManagementBinding binding;
    private FirebaseFirestore db;

    private String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TournamentManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        accessCode = getIntent().getStringExtra("ACCESS_CODE");


        }
    private void tournamentManagement() {
        binding.tournamentManagement.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(TournamentManagement.this, ConfigureTournament.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });
    }

}