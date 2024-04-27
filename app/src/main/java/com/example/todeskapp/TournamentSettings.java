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
/**
 *
 * This class provides display and functionality of the tournament_settings layout to the user.
 * Here, the user can configure the tournament settings. The user can set the tournament Name,
 * tournament game, and the bracket style.
 *
 * @author Remi_ngo
 * @author Jonathan Chen
 * @since April 2024
 *
 *
 */
public class TournamentSettings extends AppCompatActivity {
    /**
     * Binding the configure_tournament.xml file to this code.
     */
    private TournamentSettingsBinding binding;
    /**
     * Initializing Firestore database.
     */
    private FirebaseFirestore db;
    /**
     * Holds the access code variable for knowing what document to access.
     */
    private String accessCode;

    private ExecutorService executorService;
    /**
     * Sets the GUI to the tournament_settings.xml. Retrieves database information.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //sets display
        super.onCreate(savedInstanceState);
        binding = TournamentSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize database, get access code
        accessCode = getIntent().getStringExtra("ACCESS_CODE");
        db = FirebaseFirestore.getInstance();

        executorService = Executors.newSingleThreadExecutor();

        //adds functionality to the spinner that holds the brackets and save button
        bracketSpinner();
        saveButton();
    }

    /**
     * Converts the user's choice from a drop-down menu to a value to store in the document.
     *
     * @return No return value.
     *
     */
    private void bracketSpinner() {

        //converting choice to value
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tournament_format_choice, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.bracketChoices.setAdapter(adapter);
    }

    /**
     * Redirects the user back to the configure tournament page
     *
     * @return No return value.
     *
     */
    private void saveButton() {
        binding.save.setOnClickListener(v -> {
            executorService.execute(() -> {
                saveTournamentSettings();
                runOnUiThread(() -> {

                    //redirection back
                    Intent intent = new Intent(TournamentSettings.this, ConfigureTournament.class);
                    intent.putExtra("ACCESS_CODE", accessCode);  // Optionally passing the access code if needed
                    startActivity(intent);
                });
            });
        });



    }

    /**
     * Updates the database with the new tournament information
     *
     * @return No return value.
     *
     */
    private void saveTournamentSettings() {

        //gets the user input
        String tournamentName = binding.inputTournamentName.getText().toString();
        String tournamentGame = binding.inputTournamentGame.getText().toString();
        int bracketStyle = binding.bracketChoices.getSelectedItemPosition();

        //checks if it's empty
        if (tournamentName.isEmpty() || tournamentGame.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        //update the database
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
