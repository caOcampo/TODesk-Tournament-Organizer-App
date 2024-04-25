package com.example.todeskapp;

import android.annotation.SuppressLint;
import android.content.Context;
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
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String organization = document.getString("organization");
                            String rank = document.getString("organization");
                            int w = Objects.requireNonNull(document.getLong("W")).intValue();
                            int l = Objects.requireNonNull(document.getLong("L")).intValue();

                            // Create a Player object with retrieved data
                            PlayerProfile.Player player = new PlayerProfile.Player(username, organization, rank, w, l);
                            players.add(player);

                            // Add the player to the table view
                            addPlayerToTable(username, w, l);
                        }
                        // generate MatchUps
                        GeneratePlayerMatchUps();
                    } else {
                        Toast.makeText(this, "Error fetching players: " + task.getException(), Toast.LENGTH_SHORT).show();
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

    private void GeneratePlayerMatchUps() {
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
        // Implement logic to process chosen winners and update Firebase data
        // You can retrieve winner from EditText fields in each TableRow within tableLayout2
    }
}