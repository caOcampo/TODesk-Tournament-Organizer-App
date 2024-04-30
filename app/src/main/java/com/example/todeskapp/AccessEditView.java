package com.example.todeskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.AccessEditViewBinding;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class provides display and functionality of the access_edit_view.xml layout to the user.
 * After entering an access code of a previous generated tournament, it will direct you to this
 * page and will prompt the user with buttons whether or not they would like to edit or simply
 * view the tournament.
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */
public class AccessEditView extends AppCompatActivity {

    /**
     * Binding the access_edit_view.xml file to this code.
     */
    private AccessEditViewBinding binding;
    /**
     * Initializing Firestore database.
     */
    private FirebaseFirestore db;
    /**
     * Holds the access code variable for knowing what document to access.
     * */
    private String accessCode;

    /**
     * Sets the GUI to the access_edit_view.xml. Retrieves database information.
     * Adds functionality to edit and view button.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //sets display
        super.onCreate(savedInstanceState);
        binding = AccessEditViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize database, get access code
        db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        //get name of the tournament from database
        getTournamentName();

        //edit button will redirect to the organizer login
        binding.editButton.setOnClickListener(v -> {

            Intent intent = new Intent(AccessEditView.this, OrganizerLogin.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);

        });

        binding.viewButton.setOnClickListener(v -> {

            Intent intent = new Intent(AccessEditView.this, CurrentBracket_SAC_PDF.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);

        });

    }

    /**
     * Changes the title of the GUI that is displayed to be the Tournament Name in the database.
     * @return No return value.
     */

    private void getTournamentName() {
        //get access code document
        db.collection("AccessCodes").document(accessCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    //get the value from the TournamentName field and update the code.
                    if (documentSnapshot.exists()) {
                        String tournamentName = documentSnapshot.getString("TournamentName");
                        binding.tournamentNameView.setText(tournamentName);
                    } else {
                        Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error getting document: " + e.toString(), Toast.LENGTH_SHORT).show();
                });
    }



}
