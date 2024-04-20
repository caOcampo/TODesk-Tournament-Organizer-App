package com.example.todeskapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todeskapp.databinding.AccountChoiceBinding;


public class AccountChoice extends AppCompatActivity {

    private AccountChoiceBinding binding;

    private String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AccountChoiceBinding.inflate(getLayoutInflater()); // Initialize the binding
        setContentView(binding.getRoot());

        binding.newAccount.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            redirectToAnotherActivity(accessCode);
        });



        binding.existingAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AccountChoice.this, LoginAccount.class);
            startActivity(intent);
        });

    }
    private void redirectToAnotherActivity(String accessCode) {
        Intent intent = new Intent(this, CreateAccount.class);
        intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
        startActivity(intent);
    }
}
