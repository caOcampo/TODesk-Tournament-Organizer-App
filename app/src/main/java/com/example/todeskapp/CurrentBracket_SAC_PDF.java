package com.example.todeskapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.CurrentBracketSacPdfBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;

public class CurrentBracket_SAC_PDF extends AppCompatActivity {

    private CurrentBracketSacPdfBinding binding;
    private FirebaseFirestore db;
    private Button saveAsPdfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CurrentBracketSacPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // Find the ScrollView in the layout
        ScrollView scrollView = findViewById(R.id.current_bracket);

        // Read the AccessCode from Firebase
        readAccessCodeFromFirebase(scrollView);

        // Set OnClickListener to handle button click
        saveAsPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAsPdf(scrollView);
            }
        });

    }

    private void readAccessCodeFromFirebase(ScrollView scrollView) {
        db.collection("AccessCodes")
                .document("YOUR_ACCESS_CODE_DOCUMENT_ID") // REPLACE WITH ACTUAL THING
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Check if the AccessCode contains keywords
                            String accessCode = document.getString("accessCode");
                            if (accessCode != null) {
                                if (accessCode.contains("Round Robin")) {
                                    displayRoundRobinPre(scrollView);
                                } else if (accessCode.contains("Double Elimination")) {
                                    displayElimMatch(scrollView);
                                } else if (accessCode.contains("Swiss Stage")) {
                                    displaySwissStage(scrollView);
                                }
                            }
                        }
                    }
                });
    }

    private void displayRoundRobinPre(ScrollView scrollView) {
        // Inflate the layout "round_robin_pre" and add it to the ScrollView
        LayoutInflater inflater = LayoutInflater.from(this);
        View roundRobinPreView = inflater.inflate(R.layout.round_robin_pre, scrollView, false);
        scrollView.addView(roundRobinPreView);
    }

    private void displaySwissStage(ScrollView scrollView) {
        // Implement displaySwissStage if needed
    }

    private void displayElimMatch(ScrollView scrollView) {
        // Implement displayElimMatch if needed
    }

    public void saveAsPdf(ScrollView scrollView) {
        try {
            // Create a Bitmap from the ScrollView content
            Bitmap bitmap = Bitmap.createBitmap(scrollView.getChildAt(0).getWidth(), scrollView.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            scrollView.draw(canvas);

            // Create a File object for the PDF
            File pdfFile = new File(Environment.getExternalStorageDirectory(), "Tournament.pdf");
            FileOutputStream outputStream = new FileOutputStream(pdfFile);

            // Convert the Bitmap to PDF and save it
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.close();

            // Show a toast indicating the PDF is saved
            Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}


