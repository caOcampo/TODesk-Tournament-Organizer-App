package com.example.todeskapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoundRobinEdit extends AppCompatActivity {

    private FirebaseFirestore db;
    private TableLayout tableLayout1;
    private TableLayout tableLayout2;
    private String accessCode;
    private List<PlayerProfile.Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_robin_edit);

        db = FirebaseFirestore.getInstance();
        tableLayout1 = findViewById(R.id.tableLayout1);
        tableLayout2 = findViewById(R.id.tableLayout2);
        players = new ArrayList<>();

        fetchAndDisplayPlayers();
    }

    private void fetchAndDisplayPlayers() {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String username = document.getString("username");
                            String organization = document.getString("organization");
                            String rank = document.getString("rank");
                            int win = Objects.requireNonNull(document.getLong("W")).intValue();
                            int loss = Objects.requireNonNull(document.getLong("L")).intValue();

                            // Create a Player object with retrieved data
                            PlayerProfile.Player player = new PlayerProfile.Player(username, organization, rank, win, loss);
                            players.add(player);

                            // Add the player to the table view
                            addPlayerToTable(username, win, loss);
                        }
                        // generate MatchUps
                        generatePlayerMatchUps();
                    } else {
                        Toast.makeText(this, "Error fetching players: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPlayerToTable(String playerName, int w, int l) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        TextView playerNameTextView = createTextView(playerName);
        TextView wTextView = createTextView(String.valueOf(w));
        TextView lTextView = createTextView(String.valueOf(l));

        row.addView(playerNameTextView);
        row.addView(wTextView);
        row.addView(lTextView);

        tableLayout1.addView(row);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(layoutParams);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(16);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private void generatePlayerMatchUps() {
        // Iterate through all players
        for (int i = 0; i < players.size(); i++) {
            // Iterate through all other players
            for (int j = i + 1; j < players.size(); j++) {
                String matchUpText = players.get(i).getUsername() + " vs " + players.get(j).getUsername();
                addMatchUpRow(matchUpText);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void addMatchUpRow(String matchUpText) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        // TextView for displaying player matchup
        TextView matchupTextView = new TextView(this);
        matchupTextView.setText(matchUpText);
        matchupTextView.setTextColor(getResources().getColor(android.R.color.white));
        matchupTextView.setTextSize(16);
        matchupTextView.setPadding(8, 8, 8, 8);

        // TextView for displaying "Winner:"
        TextView winnerLabelTextView = new TextView(this);
        winnerLabelTextView.setText("Winner:");
        winnerLabelTextView.setTextColor(getResources().getColor(android.R.color.white));
        winnerLabelTextView.setTextSize(16);
        winnerLabelTextView.setPadding(8, 8, 8, 8);

        // EditText for inputting the winning player
        EditText winnerEditText = new EditText(this);
        winnerEditText.setId(View.generateViewId());
        winnerEditText.setHint("Enter winner");
        winnerEditText.setTextColor(getResources().getColor(android.R.color.white));
        winnerEditText.setTextSize(16);
        winnerEditText.setPadding(8, 8, 8, 8);

        // Add views to the row
        row.addView(matchupTextView);
        row.addView(winnerLabelTextView);
        row.addView(winnerEditText);

        // Add the row to the table layout
        tableLayout2.addView(row);
    }

    public void submitResults(View view) {
        // Iterate through each TableRow in tableLayout2
        for (int i = 0; i < tableLayout2.getChildCount(); i++) {
            View rowView = tableLayout2.getChildAt(i);
            if (rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                // Find the EditText in the TableRow
                EditText winnerEditText = row.findViewById(row.getChildAt(2).getId());
                String winner = winnerEditText.getText().toString().trim();
                // Find the players involved in the match-up
                String matchUpText = ((TextView) row.getChildAt(0)).getText().toString();
                String[] playersInvolved = matchUpText.split(" vs ");
                // Update the wins and losses accordingly
                for (PlayerProfile.Player player : players) {
                    if (player.getUsername().equals(winner)) {
                        player.incrementWins();
                    } else if (player.getUsername().equals(playersInvolved[0]) || player.getUsername().equals(playersInvolved[1])) {
                        player.incrementLosses();
                    }
                }
                saveWinnerToFirebase(matchUpText, winner);
            }
        }
        // After updating the players' records, you can proceed to update the Firebase database
        updatePlayersInFirebase();

        Intent intent = new Intent(this, RoundRobinPreTesting.class);
        intent.putExtra("players", (Serializable) players);
        startActivity(intent);
    }

    private void saveWinnerToFirebase(String matchUpText, String winner) {
        // Split the matchUpText to get the names of the players involved
        String[] playersInvolved = matchUpText.split(" vs ");
        String player1Name = playersInvolved[0];
        String player2Name = playersInvolved[1];

        // Update the matchup document with the winner information
        db.collection("AccessCodes").document(accessCode)
                .collection("Matchups").document(matchUpText)
                .update(
                        "winner", winner,
                        "player1", player1Name,
                        "player2", player2Name
                )
                .addOnSuccessListener(aVoid -> {
                    // Handle success if needed
                })
                .addOnFailureListener(e -> {
                    // Handle failure if needed
                    Toast.makeText(this, "Failed to update winner data in Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void updatePlayersInFirebase() {
        // Update Firebase with the changes for each player
        for (PlayerProfile.Player player : players) {
            db.collection("AccessCodes").document(accessCode)
                    .collection("PlayerList").document(/* Pass player ID here */)
                    .update(
                            "username", player.getUsername(),
                            "organization", player.getOrganization(),
                            "rank", player.getRank(),
                            "W", player.getWins(),
                            "L", player.getLosses()
                    )
                    .addOnSuccessListener(aVoid -> {
                        // Handle success if needed
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure if needed
                        Toast.makeText(this, "Failed to update player data in Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
