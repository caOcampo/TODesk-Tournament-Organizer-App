package com.example.todeskapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todeskapp.databinding.AccountChoiceBinding;


public class AccountChoice extends AppCompatActivity {

    private AccountChoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AccountChoiceBinding.inflate(getLayoutInflater()); // Initialize the binding
        setContentView(binding.getRoot());

        binding.newAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AccountChoice.this, CreateAccount.class);
            startActivity(intent);
        });

        binding.existingAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AccountChoice.this, LoginAccount.class);
            startActivity(intent);
        });

    }
}
