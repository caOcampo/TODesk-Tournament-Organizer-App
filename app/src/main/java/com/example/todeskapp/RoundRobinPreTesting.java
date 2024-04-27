package com.example.todeskapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.todeskapp.databinding.RoundRobinPreTestingBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoundRobinPreTesting extends AppCompatActivity {

    private FirebaseFirestore db;
    private RoundRobinPreTestingBinding binding;
    private TableLayout tableLayout1;
    TableLayout tableLayout2;
    private String accessCode;
    List<PlayerProfile.Player> players;

    /**
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RoundRobinPreTestingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // Find the ConstraintLayout with ID round_robin_pre
        ConstraintLayout constraintLayout = findViewById(R.id.round_robin_pre_testing);
        // Find tableLayout1 and tableLayout2 within the ConstraintLayout
        tableLayout1 = constraintLayout.findViewById(R.id.tableLayout1);
        tableLayout2 = constraintLayout.findViewById(R.id.tableLayout2);


        // Retrieve the access code from the intent extras
        accessCode = getIntent().getStringExtra("ACCESS_CODE");

        if (accessCode != null) {
            fetchAndDisplayPlayers(accessCode);
        } else {
            Toast.makeText(this, "Access code not found.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Used to fetch player's values from firebase, place them into new objects
     * where their values will be displayed into tablelayout1 & tablelayout2.
     */
    void fetchAndDisplayPlayers(String accessCode) {

        // Get the collection reference
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> playerNames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve player data from the document
                            String username = document.getString("username");
                            String organization = document.getString("organization");
                            String rank = document.getString("rank");
                            Long win = document.getLong("win");
                            Long loss = document.getLong("loss");


                            // Check if any essential data is null
                            if (username != null && win != null && loss != null) {
                                // Handle potential null values for non-essential fields like organization and rank
                                if (organization == null) {
                                    organization = "";
                                }
                                if (rank == null) {
                                    rank = "";
                                }

                                // Create a Player object with retrieved data
                                PlayerProfile.Player player = new PlayerProfile.Player(username, organization, rank, win.intValue(), loss.intValue());
                                players.add(player);

                                // Add the player info to the table view
                                addPlayerToTable(username, win.intValue(), loss.intValue());
                            } else {
                                // Handle missing essential data
                                Log.e(TAG, "Player data is incomplete or null");
                            }
                        }

                        // After retrieving and processing players, proceed to generate matchups and start the activity
                        addRowsToTable2();
                        startCurrentBracket_SAC_PDFActivity();
                    } else {
                        // Handle errors in fetching data
                        Log.e(TAG, "Error fetching players: ", task.getException());
                        Toast.makeText(this, "Error fetching players: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    void startCurrentBracket_SAC_PDFActivity() {
        Intent intent = new Intent(RoundRobinPreTesting.this, CurrentBracket_SAC_PDF.class);
        intent.putExtra("accessCode", accessCode); // Pass the access code to the RoundRobinPreTesting activity
        startActivity(intent);
        finish(); // Finish the CurrentBracket_SAC_PDF activity to prevent going back to it
    }


    /**
     * This function takes the values of the retrieved data from firebase (playerName, win & loss).
     * For each player object, a row will be dynamically made in tablelayout1 containing the values
     * playerName, wins, & loss.
     *
     * @param username a string pulled from firebase and place into a player object
     * @param win      an integer pulled from firebase and place into a player object
     * @param loss     an integer pulled from firebase and place into a player object
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
     * This function creates a text that will be imported into addPlayerToTable. This sets the parameters
     * of the text font, size and padding.
     *
     * @param text TBD
     * @return textView
     */
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setPadding(8, 8, 8, 8);
        textView.setText(text);
        return textView;
    }

    void addRowsToTable2() {
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                String matchUpText = players.get(i).getUsername() + " vs " + players.get(j).getUsername();
                addMatchUpRow(matchUpText);
            }
        }
    }

    /**
     * This function is used to dynamically create the rows in tablelayout2 based on the amount of players in
     * the bracket.
     *
     * @param matchUpText the generated match up created from addRowsToTable2
     */

// Adjust addMatchUpRow method to ensure proper text display
    @SuppressLint("SetTextI18n")
    void addMatchUpRow(String matchUpText) {

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
    }
}


