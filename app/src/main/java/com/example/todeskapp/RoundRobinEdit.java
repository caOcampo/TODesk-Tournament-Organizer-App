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

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */

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

    /**
     * Used to fetch player's values from firebase, place them into new objects
     * where their values will be manipulated by other classes and updated back into firebase.
     */
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
                            int win = Math.toIntExact((document.getLong("W")));
                            int loss = Math.toIntExact((document.getLong("L")));

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

    /**
     *
     * This function takes the values of the retrieved data from firebase (playerName, win & loss).
     * For each player object, a row will be dynamically made in tablelayout1 containing the values
     * playerName, wins, & loss.
     *
     * @param username a string pulled from firebase and place into a player object
     * @param win an integer pulled from firebase and place into a player object
     * @param loss an integer pulled from firebase and place into a player object
     */
    private void addPlayerToTable(String username, int win, int loss) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        TextView playerNameTextView = createTextView(username);
        TextView wTextView = createTextView(String.valueOf(win));
        TextView lTextView = createTextView(String.valueOf(loss));

        row.addView(playerNameTextView);
        row.addView(wTextView);
        row.addView(lTextView);

        tableLayout1.addView(row);
    }

    /**
     * This function creates a text that will be imported into addPlayerToTable. This sets the perameters
     * of the text font, size and padding.
     * @param text TBD
     * @return textView
     */
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


    /**
     * The function generatePlayerMatchups create a for loop that goes though all of the object player's
     * username from firebase and match them against one another. This will then call the function addMatchUpRow.
     * This will iterate until all player usernames are used.
     */
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

    /**
     * addMatchUpRow is a function called within generatePlayerMatchUps that sets the parameters of the string
     * created. This means font, size, and padding. Within this function it creates 3 strings to display in 1 row.
     * the first string displays the player match "player 1 vs player 2", the second is a textview called "Winner:",
     * and the last string prompts a textedit where the user is able to input which player gains the "win" point and
     * which player gains the "loss" point.
     *
     * @param matchUpText is the text generated from the function generatePlayerMatchUps.
     */
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

    /**
     * This function submitResults is activated after the save button is pressed. This will iterate though all
     * the inputs made within tablelayout2, save the input winner and add a win or loss to each player in each
     * match generated. The input winner string, win and loss values will be stored and updated back into firebase
     * in order to update the tournament bracket,automatically displaying the wins and losses within tablelayout1
     * and redisplay the input winner string within each match up generated.
     * @param view
     */

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

    /**
     * This function is called from submitResults and it saves the previously generated match ups and
     * winner strings into firebase. These strings will be later printed back into RoundRobinPreTesting
     * and displayed in tablelayout2.
     * @param matchUpText string taken from generateMatchUps
     * @param winner string taken from submitResults
     */

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

    /**
     * This function is called from submitResults to update the win losses of each username into firebase
     */
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
