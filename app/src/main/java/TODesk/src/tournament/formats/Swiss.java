package TODesk.src.tournament.formats;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;



public class Swiss extends AppCompatActivity {

    private String accessCode;
    private void countPlayersInTournament(String accessCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        accessCode = getIntent().getStringExtra("ACCESS_CODE");


        db.collection("AccessCodes")
                .document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // The query snapshot contains all the documents in the 'PlayerList' collection
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            int count = querySnapshot.size(); // Counting the documents
                            // Handle the count, e.g., update UI or log it
                            System.out.println("Number of players: " + count);
                        } else {
                            System.out.println("Failed to fetch player list or no players found.");
                        }
                    } else {
                        System.err.println("Query failed: " + task.getException().getMessage());
                    }
                });
    }
}
