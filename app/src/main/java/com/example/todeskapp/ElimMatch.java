package com.example.todeskapp;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import TODesk.src.tournament.formats.DElim;

public class ElimMatch extends RelativeLayout {

    private TextView player1NameTextView;
    private TextView player2NameTextView;
    private TextView player1ScoreTextView;
    private TextView player2ScoreTextView;

    public ElimMatch(Context context) {
        super(context);
        init(context);
    }

    public ElimMatch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ElimMatch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.elim_match, this);
        player1NameTextView = findViewById(R.id.player1Name);
        player2NameTextView = findViewById(R.id.player2Name);
        player1ScoreTextView = findViewById(R.id.player1Score);
        player2ScoreTextView = findViewById(R.id.player2Score);
    }

    public void setPlayers(Player player1, Player player2) {
        player1NameTextView.setText(player1.getName());
        player2NameTextView.setText(player2.getName());
    }

    public void setScores(int player1Score, int player2Score) {
        player1ScoreTextView.setText(String.valueOf(player1Score));
        player2ScoreTextView.setText(String.valueOf(player2Score));
    }
}