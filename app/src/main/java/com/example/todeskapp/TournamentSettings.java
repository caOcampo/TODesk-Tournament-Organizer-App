package com.example.todeskapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todeskapp.databinding.TournamentSettingsBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TournamentSettings extends AppCompatActivity {
    private TournamentSettingsBinding binding;
    private FirebaseFirestore db;

    private String accessCode;

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TournamentSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        accessCode = getIntent().getStringExtra("ACCESS_CODE");
        db = FirebaseFirestore.getInstance();

        executorService = Executors.newSingleThreadExecutor();

        bracketSpinner();
        saveButton();
    }

    private void bracketSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tournament_format_choice, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.bracketChoices.setAdapter(adapter);
    }

    private void saveButton() {
        binding.save.setOnClickListener(v -> {
            executorService.execute(() -> {
                saveTournamentSettings();
                runOnUiThread(() -> {
                    /* redirection back */
                    Intent intent = new Intent(TournamentSettings.this, ConfigureTournament.class);
                    intent.putExtra("ACCESS_CODE", accessCode);  // Optionally passing the access code if needed
                    startActivity(intent);
                });
            });
        });



    }

    private void saveTournamentSettings() {
        String tournamentName = binding.inputTournamentName.getText().toString();
        String tournamentGame = binding.inputTournamentGame.getText().toString();
        int bracketStyle = binding.bracketChoices.getSelectedItemPosition();

        if (tournamentName.isEmpty() || tournamentGame.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("AccessCodes").document(accessCode)
                .update("TournamentName", tournamentName,
                        "TournamentGame", tournamentGame,
                        "BracketStyle", bracketStyle)
                .addOnSuccessListener(aVoid -> Toast.makeText(TournamentSettings.this, "Settings saved successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(TournamentSettings.this, "Failed to save settings: " + e.toString(), Toast.LENGTH_SHORT).show());
    }

    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the executor when the activity is destroyed to avoid memory leaks
    }


}
