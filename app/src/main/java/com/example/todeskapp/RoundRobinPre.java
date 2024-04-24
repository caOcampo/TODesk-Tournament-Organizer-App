package com.example.todeskapp;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RoundRobinPre extends AppCompatActivity {

    private FirebaseFirestore db;
    private TableLayout tableLayout;
    private String accessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_robin_pre);

        db = FirebaseFirestore.getInstance();
        tableLayout = findViewById(R.id.tableLayout1);

        fetchAndDisplayPlayers();
    }

    private void fetchAndDisplayPlayers() {
        db.collection("AccessCodes").document(accessCode)
                .collection("PlayerList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String playerName = document.getString("name");
                                int w = document.getLong("W").intValue();
                                int l = document.getLong("L").intValue();
                                addPlayerToTable(playerName, w, l);
                            }
                        } else {
                            Toast.makeText(RoundRobinPre.this, "Error fetching players: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addPlayerToTable(String playerName, int w, int l) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        TextView playerNameTextView = new TextView(this);
        playerNameTextView.setText(playerName);
        playerNameTextView.setTextColor(getResources().getColor(android.R.color.white));
        playerNameTextView.setTextSize(16);
        playerNameTextView.setPadding(8, 8, 8, 8);
        row.addView(playerNameTextView);

        TextView wTextView = new TextView(this);
        wTextView.setText(String.valueOf(w));
        wTextView.setTextColor(getResources().getColor(android.R.color.white));
        wTextView.setTextSize(16);
        wTextView.setPadding(8, 8, 8, 8);
        row.addView(wTextView);

        TextView lTextView = new TextView(this);
        lTextView.setText(String.valueOf(l));
        lTextView.setTextColor(getResources().getColor(android.R.color.white));
        lTextView.setTextSize(16);
        lTextView.setPadding(8, 8, 8, 8);
        row.addView(lTextView);

        tableLayout.addView(row);
    }
}
