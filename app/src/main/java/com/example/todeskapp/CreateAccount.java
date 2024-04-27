package com.example.todeskapp;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.CreateAccountBinding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
/**
 * This class provides display and functionality of the create_account.xml layout to the user.
 * Here, the user can create an account to add an tournament to.
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */
public class CreateAccount extends AppCompatActivity {

    /**
     * Binding the create_account.xml file to this code.
     */
    private CreateAccountBinding binding;
    /**
     * Initializing Firestore Authentication.
     */
    private FirebaseAuth mAuth;
    /**
     * Initializing Firestore database.
     */
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
     * Sets the GUI to the create_account.xml. Retrieves database information.
     * Adds functionality to buttons and text input for creating a new account.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //sets display
        super.onCreate(savedInstanceState);
        binding = CreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize Firebase database and authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize the executor service
        executorService = Executors.newSingleThreadExecutor();

        //get access code
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        //gets the text input for the email and password when the create button is clicked
        binding.create.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString();
            String password = binding.inputPassword.getText().toString();
            createAccount(email, password);
        });
    }

    /**
     * Creates an account for authentication with the gathered information about username and
     * password
     *
     * @return No return value.
     *
     */
    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        // Using ExecutorService to perform Firebase authentication on a background thread
        executorService.execute(() -> {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            runOnUiThread(() -> {

                                //update database with the account information in the document
                                updateFirestore(email, password);
                                Toast.makeText(CreateAccount.this, "Authentication successful.", Toast.LENGTH_SHORT).show();

                                //redirect back to configure tournament once an account has been created
                                Intent intent = new Intent(this, ConfigureTournament.class);
                                intent.putExtra("ACCESS_CODE", accessCode);
                                startActivity(intent);
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(CreateAccount.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
        });
    }

    /**
     * Updates the database with the entered email and password
     *
     * @return No return value.
     *
     */
    private void updateFirestore(String email, String password) {
        if (accessCode == null) {
            Toast.makeText(this, "Error: No access code provided.", Toast.LENGTH_LONG).show();
            return;
        }

        //input to database
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("OrganizerEmail", email);
        updates.put("OrganizerPassword", password);

        executorService.execute(() -> {
            db.collection("AccessCodes").document(accessCode)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> runOnUiThread(() -> Toast.makeText(CreateAccount.this, "Firestore updated successfully.", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(CreateAccount.this, "Error updating Firestore: " + e.toString(), Toast.LENGTH_SHORT).show()));
        });
    }

    /**
     * Checks if every part of the form has been filled out to create the account
     *
     * @return No return value.
     *
     */
    private boolean validateForm() {
        boolean valid = true;
        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();
        String confirmPassword = binding.inputPasswordConf.getText().toString();

        //checks if the fields are empty

        if (email.isEmpty()) {
            binding.inputEmail.setError("Required.");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.inputPassword.setError("Required.");
            valid = false;
        } else {
            binding.inputPassword.setError(null);
        }

        //makes sure the passwords match to confirm it
        if (!password.equals(confirmPassword)) {
            binding.inputPasswordConf.setError("Passwords do not match.");
            valid = false;
        } else {
            binding.inputPasswordConf.setError(null);
        }

        return valid;
    }


    //shuts down the executor service
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the executor service when the activity is destroyed
    }
}