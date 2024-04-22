package com.example.todeskapp;


import android.content.Intent;
import android.os.Bundle;

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
            Intent intent = new Intent(AccountChoice.this, CreateAccount.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });



        binding.existingAccount.setOnClickListener(v -> {
            accessCode = getIntent().getStringExtra("ACCESS_CODE");
            Intent intent = new Intent(AccountChoice.this, LoginAccount.class);
            intent.putExtra("ACCESS_CODE", accessCode);  // Passing the access code to the next activity
            startActivity(intent);
        });

    }
}
