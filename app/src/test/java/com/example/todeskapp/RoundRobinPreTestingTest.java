package com.example.todeskapp;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

import com.google.firebase.firestore.FirebaseFirestore;


public class RoundRobinPreTestingTest {

    private RoundRobinPreTesting roundRobinPreTesting;
    private TableLayout tableLayout2;
    private List<PlayerProfile.Player> players;

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private Intent mockIntent;

    @Mock
    private TableLayout mockTableLayout1;

    @Mock
    private TableLayout mockTableLayout2;

    private final String accessCode = "ACCESS_CODE";

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);


        // Mock the intent with access code
        when(roundRobinPreTesting.getIntent()).thenReturn(mockIntent);
        when(mockIntent.getStringExtra("ACCESS_CODE")).thenReturn(accessCode);

        // Mock findViewById for tableLayout1 and tableLayout2
        when(roundRobinPreTesting.findViewById(R.id.tableLayout1)).thenReturn(mockTableLayout1);
        when(roundRobinPreTesting.findViewById(R.id.tableLayout2)).thenReturn(mockTableLayout2);


        roundRobinPreTesting = new RoundRobinPreTesting();
        tableLayout2 = new TableLayout(roundRobinPreTesting);

        // Initialize players list with mock data
        players = new ArrayList<>();
        players.add(new PlayerProfile.Player("Player1", "Org1", "Rank1", 10, 5));
        players.add(new PlayerProfile.Player("Player2", "Org2", "Rank2", 8, 7));
        players.add(new PlayerProfile.Player("Player3", "Org3", "Rank3", 6, 9));
        roundRobinPreTesting.players = players;

        roundRobinPreTesting = new RoundRobinPreTesting();
        roundRobinPreTesting.tableLayout2 = mock(TableLayout.class);
        }




    @Test
    public void testAddRowsToTable2() {
        // Call the method to test
        roundRobinPreTesting.addRowsToTable2();

        // Verify that the correct number of rows are added to tableLayout2
        assertEquals((long) players.size() * (players.size() - 1) / 2, tableLayout2.getChildCount());

        // Verify that each row contains the expected match-up text
        int rowCounter = 0;
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                // Skip cases where i == j to avoid matching a player with themselves
                if (i != j) {
                    String expectedMatchUpText = players.get(i).getUsername() + " vs " + players.get(j).getUsername();
                    TableRow row = (TableRow) tableLayout2.getChildAt(rowCounter++);
                    TextView matchupTextView = (TextView) row.getChildAt(0);
                    assertEquals(expectedMatchUpText, matchupTextView.getText().toString());
                }
            }
        }
    }



    @Test
    public void testAddMatchUpRow() {
        // Create an instance of RoundRobinPreTesting
        RoundRobinPreTesting roundRobinPreTesting = new RoundRobinPreTesting();

        // Set up mock TextView for matchup
        TextView mockMatchupTextView = mock(TextView.class);
        when(mockMatchupTextView.getText()).thenReturn("Player1 vs Player2");

        // Set up mock TextView for winner label
        TextView mockWinnerLabelTextView = mock(TextView.class);
        when(mockWinnerLabelTextView.getText()).thenReturn("Winner:");

        // Set up mock EditText for winner input
        EditText mockWinnerEditText = mock(EditText.class);

        // Create a mock TableRow
        TableRow mockRow = mock(TableRow.class);
        when(mockRow.getChildAt(anyInt())).thenReturn(mockMatchupTextView)
                .thenReturn(mockWinnerLabelTextView)
                .thenReturn(mockWinnerEditText);

        // Set up the RoundRobinPreTesting instance with mock objects
        roundRobinPreTesting.addMatchUpRow("Player1 vs Player2");

        // Verify that the matchup TextView has the correct text
        assertEquals("Player1 vs Player2", mockMatchupTextView.getText());

        // Verify that the winner label TextView has the correct text
        assertEquals("Winner:", mockWinnerLabelTextView.getText());

        // Verify that the EditText is added to the TableRow
        verify(mockRow).addView(mockWinnerEditText);

        // Verify that the TableRow is added to the TableLayout
        verify(tableLayout2).addView(mockRow);
    }

    @Test
    public void testOnCreate() {
        // Call the onCreate method
        roundRobinPreTesting.onCreate(null);

        // Verify that the access code is retrieved from the intent extras
        verify(mockIntent).getStringExtra("ACCESS_CODE");

        // Verify that fetchAndDisplayPlayers is called with the access code
        verify(roundRobinPreTesting).fetchAndDisplayPlayers(accessCode);
    }


    @Test
    public void testStartCurrentBracket_SAC_PDFActivity() {
        // Call the method
        roundRobinPreTesting.startCurrentBracket_SAC_PDFActivity();

        // Verify that the intent is created and started
        verify(mockIntent).putExtra("accessCode", accessCode);
        verify(roundRobinPreTesting).startActivity(mockIntent);

        // Verify that the activity is finished
        verify(roundRobinPreTesting).finish();
    }
}





