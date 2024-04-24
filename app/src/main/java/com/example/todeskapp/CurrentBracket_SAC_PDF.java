package com.example.todeskapp;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;

public class CurrentBracket_SAC_PDF extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_bracket_sac_pdf);

        // Find the ScrollView in the layout
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ScrollView scrollView = findViewById(R.id.current_bracket);

        // Inflate the layout "round_robin_pre" and add it to the ScrollView
        LayoutInflater inflater = LayoutInflater.from(this);
        View roundRobinPreView = inflater.inflate(R.layout.round_robin_pre, scrollView, false);
        scrollView.addView(roundRobinPreView);
    }
}
