package com.example.todeskapp;


import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.LoginAccountBinding;
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

/**
 *
 * This class provides display and functionality of the login_account.xml layout to the user.
 * Here, the user can login into a pre-existing account that has already been authenticated
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */
public class LoginAccount extends AppCompatActivity{

    /**
     * Binding the login_account.xml file to this code.
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
     * Sets the GUI to the login_account.xml. Retrieves database information.
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

        // Initialize the executor service
        executorService = Executors.newSingleThreadExecutor();

        //get access code
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        //adds functionality to the login button
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(binding.inputEmail.getText().toString(), binding.inputPassword.getText().toString());
            }
        });
    }

    /**
     * Gets the user input for logging in
     *
     * @param email the user email
     * @param password the user password
     * @return No return value.
     *
     */
    private void loginUser(String email, String password) {

        //checks if fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginAccount.this, "Email and password are required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        //start executor service
        executorService.execute(() -> {

            //authenticate with email and password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            runOnUiThread(() -> {

                                //update the database with the email and password
                                updateFirestore(email, password);
                                Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();

                                //navigate back to configure tournament page
                                navigateToConfigureTournament();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
        });
    }

    /**
     * Updates the database with the email and password
     *
     * @param email the user email
     * @param password the user password
     * @return No return value.
     *
     */
    private void updateFirestore(String email, String password) {

        //if accessCode has a value
        if (accessCode == null) {
            Toast.makeText(this, "Error: No access code provided.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a map to hold the update data
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("OrganizerEmail", email);
        updates.put("OrganizerPassword", password);

        executorService.execute(() -> {
            db.collection("AccessCodes").document(accessCode)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> runOnUiThread(() -> Toast.makeText(this, "Firestore updated successfully.", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(this, "Error updating Firestore: " + e.toString(), Toast.LENGTH_SHORT).show()));
        });

    }

    /**
     * Redirects to the Configure Tournament menu
     *
     * @return No return value.
     *
     */
    private void navigateToConfigureTournament() {
        Intent intent = new Intent(this, ConfigureTournament.class);
        intent.putExtra("ACCESS_CODE", accessCode);
        startActivity(intent);
    }

    //shuts down the executor service
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow(); // Properly shutdown the executor when the activity is destroyed
    }
}

