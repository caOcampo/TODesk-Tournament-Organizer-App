package com.example.todeskapp;

import com.example.todeskapp.ConfigureTournament;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.AccessEditViewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


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


    ConfigureTournament brackets;

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

            /*Intent intent = new Intent(AccessEditView.this, CurrentBracket_SAC_PDF.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);*/

            db.collection("AccessCodes").document(accessCode)
                    .get()

                    //if can access
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                //gets the bracketStyle field value which is an integer that corresponds to each format

                                Long bracketStyleLong = document.getLong("BracketStyle");

                                if (bracketStyleLong != null) {
                                    int bracketStyle = bracketStyleLong.intValue();
                                    switch (bracketStyle) {

                                        // swiss pool
                                        case 0:
                                            swissPlayerValidation();
                                            break;

                                        // round robin
                                        case 1:
                                            performActionBasedOnBracketStyle1();
                                            break;
                                        case 2:
                                            performActionBasedOnBracketStyle1();
                                            break;


                                        default:
                                            Toast.makeText(this, "Unhandled bracket style: " + bracketStyle, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "BracketStyle field is missing.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Failed to fetch document: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });


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

    public void swissPlayerValidation() {
        //test to see if can retrieve access code document
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()

                //if can access
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int playerCount = task.getResult().size();

                            //only proceed to redirect if the number of players is 8 or 16
                            if (playerCount == 8 || playerCount == 16) {

                                Intent intent = new Intent(AccessEditView.this, SwissEdit.class);
                                intent.putExtra("ACCESS_CODE", accessCode);
                                startActivity(intent);

                            } else {

                                Toast.makeText(AccessEditView.this, "Swiss Pool MUST HAVE 8 or 16 players", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AccessEditView.this, "Failed to fetch players: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void performActionBasedOnBracketStyle1() {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(AccessEditView.this, CurrentBracket_SAC_PDF.class);
                            intent.putExtra("ACCESS_CODE", accessCode);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AccessEditView.this, "Failed to fetch players: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }


                });

    }


}
