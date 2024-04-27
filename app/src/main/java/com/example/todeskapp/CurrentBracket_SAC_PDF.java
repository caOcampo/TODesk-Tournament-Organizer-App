package com.example.todeskapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.CurrentBracketSacPdfBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;


public class CurrentBracket_SAC_PDF extends AppCompatActivity {

    private CurrentBracketSacPdfBinding binding;
    private FirebaseFirestore db;
    private String accessCode;
    private LinearLayout bracketContainer;


    private Button saveAsPdfButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CurrentBracketSacPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        bracketContainer = findViewById(R.id.bracket_container);


        readAccessCodeFromFirebase(bracketContainer);
        /*saveAsPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAsPdf(bracketContainer);
            }
        });*/
    }

    private void readAccessCodeFromFirebase(LinearLayout bracketContainer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the specific document reference
        db.collection("AccessCodes").document(accessCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {

                            Long bracketStyleLong = document.getLong("BracketStyle");


                            if (bracketStyleLong != null) {
                                int bracketStyle = bracketStyleLong.intValue();
                                switch (bracketStyle) {
                                    case 0:
                                        displaySwissStage(bracketContainer);
                                        break;
                                    case 1:
                                        displayRoundRobinPre(bracketContainer);
                                        break;

                                    default:
                                        Toast.makeText(this, "Unhandled bracket style: " + bracketStyle, Toast.LENGTH_SHORT).show();
                                }
                            }

                            else {
                                Toast.makeText(this, "BracketStyle field is missing.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else {
                            Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else {
                        Toast.makeText(this, "Failed to fetch document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayRoundRobinPre(LinearLayout bracketContainer) {
        // Inflate the layout "round_robin_pre_testing" and add it to the ScrollView
        LayoutInflater inflater = LayoutInflater.from(this);
        View roundRobinPreTestingView = inflater.inflate(R.layout.round_robin_pre_testing, bracketContainer, false);
        bracketContainer.addView(roundRobinPreTestingView);
    }


    private void displaySwissStage(LinearLayout bracketContainer) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View swissStagePreview = inflater.inflate(R.layout.swiss_pool_display_8p, bracketContainer, false);
        bracketContainer.addView(swissStagePreview);
        /*if(numberOfPlayers == 8){
            View swissStagePreview = inflater.inflate(R.layout.swiss_pool_display_8p, bracketContainer, false);
            bracketContainer.addView(swissStagePreview);
        }
        else{
            View swissStagePreview = inflater.inflate(R.layout.swiss_pool_display_16p, bracketContainer, false);
            bracketContainer.addView(swissStagePreview);
        }*/

    }

    private void displayElimMatch(LinearLayout scrollView) {


    }

    /*public void saveAsPdf(LinearLayout bracketContainer) {
        try {
            // Create a Bitmap from the ScrollView content
            Bitmap bitmap = Bitmap.createBitmap(bracketContainer.getChildAt(0).getWidth(), bracketContainer.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            bracketContainer.draw(canvas);

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
    }*/

}