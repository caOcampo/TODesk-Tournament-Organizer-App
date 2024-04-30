package com.example.todeskapp;


import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.Button;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todeskapp.databinding.CurrentBracketSacPdfBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import java.io.File;
import java.io.FileOutputStream;


public class CurrentBracket_SAC_PDF extends AppCompatActivity {

    private CurrentBracketSacPdfBinding binding;
    private FirebaseFirestore db;
    private String accessCode;
    private LinearLayout bracketContainer;

    private ProgressBar progressBar;


    private Button saveAsPdfButton;

    Toast showAccessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CurrentBracketSacPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        bracketContainer = findViewById(R.id.bracket_container);

        progressBar = findViewById(R.id.progressBar);

        readAccessCodeFromFirebase();
        homeButton();

        saveAsPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAsPdf(bracketContainer);
            }
        });

        binding.SaveAccessCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CurrentBracket_SAC_PDF.this, accessCode, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void readAccessCodeFromFirebase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("AccessCodes").document(accessCode)

                .get(Source.SERVER)

                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {

                            Long bracketStyleLong = document.getLong("BracketStyle");

                            if (bracketStyleLong != null) {

                                int bracketStyle = bracketStyleLong.intValue();

                                switch (bracketStyle) {
                                    case 0:
                                        displaySwissStage();
                                        break;
                                    case 1:
                                        displayRoundRobinPre();
                                        break;
                                    default:
                                        Toast.makeText(this, "Unhandled bracket style: " + bracketStyle, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "BracketStyle field is missing.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch document: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveAsPdf(LinearLayout bracketContainer) {
        //get access code document
        db.collection("AccessCodes").document(accessCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tournamentName = documentSnapshot.getString("TournamentName");

                        PdfDocument pdfDocument = new PdfDocument();
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bracketContainer.getChildAt(0).getWidth(),
                                bracketContainer.getChildAt(0).getHeight(), 1).create();
                        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                        bracketContainer.draw(page.getCanvas());
                        pdfDocument.finishPage(page);

                        // Using getExternalFilesDir for compatibility with scoped storage
                        File pdfFile = new File(getExternalFilesDir(null), tournamentName + ".pdf");

                        try {
                            FileOutputStream outputStream = new FileOutputStream(pdfFile);
                            pdfDocument.writeTo(outputStream);
                            outputStream.close();
                            pdfDocument.close();

                            // Include the path in the toast message
                            Toast.makeText(this, "PDF saved successfully at " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error getting document: " + e.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    private void displayRoundRobinPre() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View roundRobinPreTestingView = inflater.inflate(R.layout.round_robin_pre_testing, bracketContainer, false);
        bracketContainer.addView(roundRobinPreTestingView);
    }

    public void displaySwissStage() {
        showLoadingIndicator();
        fetchDataAsync(new DataCallback() {
            @Override
            public void onDataReady(Data data) {
                hideLoadingIndicator();
                bracketContainer.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(CurrentBracket_SAC_PDF.this);
                View swissStagePreview = inflater.inflate(R.layout.swiss_pool_display_8p, bracketContainer, false);
                bracketContainer.addView(swissStagePreview);
            }
        });
    }

    private void displayElimMatch(LinearLayout scrollView) {
    }

    private void showLoadingIndicator() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        progressBar.setVisibility(View.GONE);
    }

    private void fetchDataAsync(DataCallback callback) {
        // Simulate fetching data asynchronously
        new Thread(() -> {
            // Simulate network delay
            try {
                Thread.sleep(2000); // Delay for 2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Assume data is fetched successfully
            Data data = new Data(); // Replace this with actual data fetching logic
            runOnUiThread(() -> callback.onDataReady(data));
        }).start();
    }

    interface DataCallback {
        void onDataReady(Data data);
    }

    class Data {
        // Define data fields here
    }

    private void homeButton() {
        binding.homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CurrentBracket_SAC_PDF.this, MainMenu.class);
            intent.putExtra("ACCESS_CODE", accessCode);
            startActivity(intent);
        });

    }


    /*public void saveAsPdf(LinearLayout bracketContainer) {
        try {
            // Create a Bitmap from the ScrollView content
            Bitmap bitmap = Bitmap.createBitmap(bracketContainer.getChildAt(0).getWidth(), bracketContainer.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            bracketContainer.draw(canvas);

            // Create a File object for the PDF
            String fileName = ""
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