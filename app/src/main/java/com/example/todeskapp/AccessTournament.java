package com.example.todeskapp;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import com.example.todeskapp.databinding.AccessTournamentBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
/**
 * This class provides display and functionality of the access_tournament.xml layout to the user.
 * Here, the user can enter an access code to connect to a previously generated tournament.
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */
public class AccessTournament extends AppCompatActivity {

    /**
     * Binding the access_tournament.xml file to this code.
     */
    private AccessTournamentBinding binding;
    /**
     * Initializing Firestore database.
     */
    private FirebaseFirestore db;
    /**
     * Holds the access code variable for knowing what document to access.
     */
    private String validAccessCode;

    /**
     * Sets the GUI to the access_tournament.xml. Retrieves database information.
     * Adds functionality to login button.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //sets display
        super.onCreate(savedInstanceState);
        binding = AccessTournamentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize database
        db = FirebaseFirestore.getInstance();

        //gets user input, if checks access code is valid
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessCode = binding.editTextTextPassword.getText().toString();
                checkAccessCode(accessCode);
            }
        });
    }


    /**
     * Checks if the access code that the user inputted is a valid access code that exists in the
     * database. If it does, it redirects user to decide if they want to edit or access that
     * tournament
     *
     * @param accessCode access code of the tournament
     * @return No return value.
     *
     */
    private void checkAccessCode(String accessCode) {
        //test to see if can retrieve access code document
        db.collection("AccessCodes")
                .document(accessCode)
                .get()

                //if can access
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();

                        //access code is valid and exists in the database
                        if (document.exists()) {

                            //if access code is valid redirect to AccessEditView
                            validAccessCode = accessCode;
                            Toast.makeText(AccessTournament.this, "Access Code is valid!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AccessTournament.this, AccessEditView.class);
                            intent.putExtra("ACCESS_CODE", validAccessCode);
                            startActivity(intent);

                        } else {

                            Toast.makeText(AccessTournament.this, "Invalid Access Code.", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(AccessTournament.this, "Error checking document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}