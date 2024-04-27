package com.example.todeskapp;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.LoginAccountBinding;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todeskapp.databinding.PlayerDisplayBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
/**
 *
 * This class provides display and functionality of the organizer_login.xml layout to the user.
 * When trying to access an existing tournament, the user must log into the correct account
 * that was used to create the tournament. Here, the user can login into that pre-existing account
 * that has already been authenticated.
 *
 * @author Remi_ngo
 * @since April 2024
 *
 */
public class OrganizerLogin extends AppCompatActivity{

    /**
     * Binding the organizaer_login.xml file to this code.
     */
    private LoginAccountBinding binding;

    /**
     * Initializing Firestore database and authentication.
     */
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    /**
     * Holds the access code variable for knowing what document to access.
     */
    private String accessCode;
    /**
     * Executor service to run tasks in the background off the main thread
     */
    private ExecutorService executorService;

    /**
     * Sets the GUI to the organizer_login.xml. Retrieves database information.
     * Adds functionality to the login button.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //sets display
        super.onCreate(savedInstanceState);
        binding = LoginAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize Firebase database and authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //get access code
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        // Initialize the executor service
        executorService = Executors.newSingleThreadExecutor();
        binding.loginPrompt2.setText("to EDIT");

        // get the email and password from the input text
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.inputEmail.getText().toString();
                String password = binding.inputPassword.getText().toString();
                loginUser(email, password);
            }
        });
    }

    /**
     * Get the email and password inputed and validates it
     *
     * @param email the user email
     * @param password the user password
     * @return No return value.
     *
     */
    private void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkAccountDetails(email, password);
                    } else {
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Checks if the account details matches the access code document.
     *
     * @param email the user email
     * @param password the user password
     * @return No return value.
     *
     */
    private void checkAccountDetails(String email, String password) {

        executorService.execute(() -> {

            //test to see if can retrieve access code document/
            db.collection("AccessCodes").document(accessCode)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            String authEmail = documentSnapshot.getString("OrganizerEmail");
                            String authPassword = documentSnapshot.getString("OrganizerPassword");

                            //if the email and password match the database details
                            if (email.equals(authEmail) && password.equals(authPassword)) {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
                                    navigateToConfigureTournament();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Account details are not correct! Try Again!", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "No such access code exists.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .addOnFailureListener(e -> runOnUiThread(() -> {
                        Toast.makeText(this, "Error accessing Firestore: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }));
        });
    }

    /**
     * Redirects to Tournament Management if login is successful
     *
     * @return No return value.
     *
     */
    private void navigateToConfigureTournament() {
        Intent intent = new Intent(OrganizerLogin.this, TournamentManagement.class);
        intent.putExtra("ACCESS_CODE", accessCode);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuth != null) mAuth.signOut();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
