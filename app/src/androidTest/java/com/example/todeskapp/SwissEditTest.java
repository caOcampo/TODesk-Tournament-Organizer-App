package com.example.todeskapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.core.app.ActivityScenario;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import android.content.Intent;
import static org.junit.Assert.*;



@RunWith(AndroidJUnit4.class)
public class SwissEditTest {
    private FirebaseFirestore db;
    private DocumentReference docRef;
    //test tournament
    String accessCode = "5IE83";

    @Before
    public void setUp() {
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("AccessCodes").document("5IE83");

    }

    //if the number of matches are 16, pass
    @Test
    public void testNumberOfMatches() {

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SwissEdit.class);
        intent.putExtra("ACCESS_CODE", accessCode);

        try (ActivityScenario<SwissEdit> scenario = ActivityScenario.launch(intent)) {

            scenario.onActivity(activity -> {

                activity.swissBracketStructure(accessCode);
                db.collection("AccessCodes")
                        .document(accessCode)
                        .collection("00")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                int documentCount = task.getResult().size();
                                assertEquals(16, documentCount);
                            } else {
                                fail("Failed to fetch the documents: " + task.getException().getMessage());
                            }
                        });
            });
        }
    }
    public void testMatchesFieldValue() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SwissEdit.class);
        intent.putExtra("ACCESS_CODE", accessCode);

        try (ActivityScenario<SwissEdit> scenario = ActivityScenario.launch(intent)) {

            scenario.onActivity(activity -> {
                activity.initializeMatches(accessCode);

                String[] collectionNames = new String[]{"00", "10", "01", "20", "11", "02", "30", "21", "12", "03", "31", "22", "13", "32", "23"};

                for (String collectionName : collectionNames) {
                    db.collection("AccessCodes")
                            .document(accessCode)
                            .collection(collectionName)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        assertTrue(document.contains("player1"));
                                        assertTrue(document.contains("player2"));
                                        assertTrue(document.contains("winRedirect"));
                                        assertTrue(document.contains("lossRedirect"));
                                    }
                                } else {
                                    fail("Failed to fetch the documents: " + task.getException().getMessage());
                                }
                            });
                }
            });
        }
    }


}