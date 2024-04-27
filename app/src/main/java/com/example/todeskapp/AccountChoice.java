package com.example.todeskapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todeskapp.databinding.AccountChoiceBinding;

/**
 * This class provides display and functionality of the account_choice.xml layout to the user.
 * Here, the user can choose whether or not to create a tournament on an existing
 * authenitcated account or a new account.
 *
 * @author Remi_ngo
 * @since April 2024
 *
 *
 */
public class AccountChoice extends AppCompatActivity {

    /**
     * Binding the account_choice.xml file to this code.
     */
    private AccountChoiceBinding binding;
    /**
     * Holds the access code variable for knowing what document to access.
     */
    private String accessCode;

    /**
     * Sets the GUI to the account_choice.xml. Retrieves database information.
     * Adds functionality to buttons for creating a new account and logging into a new one.
     *
     * @param savedInstanceState the state of the GUI
     * @return No return value.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets display
        super.onCreate(savedInstanceState);
        binding = AccountChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //create account button
        binding.newAccount.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(AccountChoice.this, CreateAccount.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);
        });

        //login account button
        binding.existingAccount.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(AccountChoice.this, LoginAccount.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);
        });

    }
}
