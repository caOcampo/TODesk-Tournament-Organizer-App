package com.example.todeskapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class RoundRobinPreTesting extends AppCompatActivity {

    private FirebaseFirestore db;
    private TableLayout tableLayout1;
    private TableLayout tableLayout2;
    private String accessCode;
    private List<PlayerProfile.Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = FirebaseFirestore.getInstance();

        // Find the ConstraintLayout with ID round_robin_pre
        ConstraintLayout constraintLayout = findViewById(R.id.round_robin_pre_testing);
        // Find tableLayout1 and tableLayout2 within the ConstraintLayout
        tableLayout1 = constraintLayout.findViewById(R.id.tableLayout1);
        tableLayout2 = constraintLayout.findViewById(R.id.tableLayout2);

        // Retrieve the access code from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessCode = extras.getString("accessCode");
        }

        if (accessCode != null) {
            fetchAndDisplayPlayers();
        } else {
            Toast.makeText(this, "Access code not found", Toast.LENGTH_SHORT).show();
        }


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

                            // Add the player info to the table view
                            addPlayerToTable(username, win, loss);
                        }
                        // Generate MatchUps
                        addRowsToTable2(); // Move this line outside the loop

                        // Set content view after all data retrieval and processing
                        setContentView(R.layout.round_robin_pre_testing);
                    } else {
                        Toast.makeText(this, "Error fetching players: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void addPlayerToTable(String username, int w, int l) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        TextView playerNameTextView = createTextView(username);
        TextView wTextView = createTextView(String.valueOf(w));
        TextView lTextView = createTextView(String.valueOf(l));

        row.addView(playerNameTextView);
        row.addView(wTextView);
        row.addView(lTextView);

        tableLayout1.addView(row);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setPadding(8, 8, 8, 8);
        textView.setText(text);
        return textView;
    }


    private void addRowsToTable2() {
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                String matchUpText = players.get(i).getUsername() + " vs " + players.get(j).getUsername();
                addMatchUpRow(matchUpText);
            }
        }
    }

    // Adjust addMatchUpRow method to ensure proper text display
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
        matchupTextView.setMaxWidth(195); // Set a maximum width for the TextView
        matchupTextView.setEllipsize(TextUtils.TruncateAt.END);
        matchupTextView.setSingleLine(true); // Truncate text to a single line if it exceeds width

        // TextView for displaying "Winner:"
        TextView winnerLabelTextView = new TextView(this);
        winnerLabelTextView.setText("Winner:");
        winnerLabelTextView.setTextColor(getResources().getColor(android.R.color.white));
        winnerLabelTextView.setTextSize(16);
        winnerLabelTextView.setPadding(8, 8, 8, 8);

        // EditText for inputting the winning player
        EditText winnerEditText = new EditText(this);
        winnerEditText.setId(View.generateViewId());
        winnerEditText.setTextColor(getResources().getColor(android.R.color.white));
        winnerEditText.setTextSize(16);
        winnerEditText.setPadding(8, 8, 8, 8);

        // Add views to the row
        row.addView(matchupTextView);
        row.addView(winnerLabelTextView); // Add this line to include the "Winner:" TextView
        row.addView(winnerEditText); // Add this line to include the EditText for inputting the winning player

        // Add the row to the table layout
        tableLayout2.addView(row);

        // Set a listener to update the EditText with the winner's username
        winnerEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                EditText editText = (EditText) v;
                String winner = editText.getText().toString().trim();
                if (!winner.isEmpty()) {
                    // Update the winner EditText field
                    editText.setText(winner);
                }
            }
        });
    }
}
