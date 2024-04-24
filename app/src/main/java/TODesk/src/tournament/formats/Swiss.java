/*package TODesk.src.tournament.formats;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import java.util.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;



public class Swiss extends AppCompatActivity {

    private FirebaseFirestore db;

    public Swiss() {
        db = FirebaseFirestore.getInstance();
    }
    public void initializeSwissStage(String accessCode, int stageNumber) {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        List<String> playerNames = new ArrayList<>();

                        task.getResult().forEach(document -> {
                            playerNames.add(document.getId());
                        });


                        Collections.shuffle(playerNames);


                        int rounds = playerNames.size() / 2;

                        *//*System.out.println("Number of rounds: " + rounds);*//*


                        initializePlayerStats(accessCode, playerNames);
                        matchMaking(accessCode, playerNames, stageNumber);

                    } else {
                        System.err.println("Error fetching players: " + task.getException().getMessage());
                    }
                });
    }

    private void initializePlayerStats(String accessCode, List<String> playerNames) {
        for (String playerName : playerNames) {
            db.collection("AccessCodes")
                    .document(accessCode)
                    .collection("PlayerList")
                    .document(playerName)
                    .update("win", 0, "loss", 0)
                    .addOnSuccessListener(aVoid -> System.out.println("Player stats updated for: " + playerName))
                    .addOnFailureListener(e -> System.err.println("Error updating player stats: " + e.getMessage()));
        }
    }

    private void matchMaking(String accessCode, List<String> playerNames, int stageNumber) {
        int numberOfPlayers = playerNames.size();

        for (int i = 0; i < numberOfPlayers; i += 2) {

            String player1 = playerNames.get(i);
            String player2 = playerNames.get(i + 1);

            *//*Create a matchup document or log
            System.out.println("Matchup: " + player1 + " vs " + player2);*//*

            // Optionally create or update a match document
            db.collection("AccessCodes")
                    .document(accessCode)
                    .collection("Matchups Stage: "+ stageNumber)
                    .add(new Match(player1, player2))
                    .addOnSuccessListener(documentReference -> System.out.println("Match created between " + player1 + " and " + player2))
                    .addOnFailureListener(e -> System.err.println("Failed to create match: " + e.getMessage()));
        }
    }

    class Match {
        String player1;
        String player2;

        Match(String player1, String player2) {
            this.player1 = player1;
            this.player2 = player2;
        }
    }
}*/
