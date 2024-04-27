package com.example.todeskapp;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CurrentBracket_SAC_PDFTest {

    private CurrentBracket_SAC_PDF currentBracketSacPdf;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mock getIntent() and set extras
        Intent mockIntent = mock(Intent.class);
        when(currentBracketSacPdf.getIntent()).thenReturn(mockIntent);
        when(mockIntent.getStringExtra("ACCESS_CODE")).thenReturn("ACCESS_CODE");
        when(mockIntent.getIntExtra("NUMBER_OF_PLAYERS", 0)).thenReturn(8); // Or any desired value

        // Mock the LinearLayout
        LinearLayout mockLinearLayout = mock(LinearLayout.class);
        when(currentBracketSacPdf.findViewById(R.id.bracket_container)).thenReturn(mockLinearLayout);

        // Mock FirebaseFirestore.getInstance()
        FirebaseFirestore mockFirestore = mock(FirebaseFirestore.class);


        // Mock Firebase document
        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getLong("BracketStyle")).thenReturn(0L); // Or any desired value

        // Mock Firebase task
        Task<DocumentSnapshot> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);

        // Mock FirebaseFirestore collection, document, and get methods
        CollectionReference mockCollectionReference = mock(CollectionReference.class);
        when(mockFirestore.collection("AccessCodes")).thenReturn(mockCollectionReference);
        DocumentReference mockDocumentReference = mock(DocumentReference.class);
        when(mockCollectionReference.document("ACCESS_CODE")).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);

        // Handle different cases for displayRoundRobinPre and displaySwissStage
        LayoutInflater mockInflater = mock(LayoutInflater.class);
        when(mockInflater.inflate(eq(R.layout.round_robin_pre_testing), (ViewGroup) any(ViewGroup.class), eq(false))).thenReturn(mock(View.class));
        when(mockInflater.inflate(eq(R.layout.swiss_pool_display_8p), (ViewGroup) any(ViewGroup.class), eq(false))).thenReturn(mock(View.class));
        when(mockInflater.inflate(eq(R.layout.swiss_pool_display_16p), (ViewGroup) any(ViewGroup.class), eq(false))).thenReturn(mock(View.class));
    }

    @Test
    public void testDisplayRoundRobinPre() {
        // Mock LinearLayout and LayoutInflater
        LinearLayout mockBracketContainer = mock(LinearLayout.class);
        LayoutInflater mockInflater = mock(LayoutInflater.class);

        // Mock inflated view
        View mockRoundRobinPreTestingView = mock(View.class);
        when(mockInflater.inflate(eq(R.layout.round_robin_pre_testing), (ViewGroup) any(ViewGroup.class), eq(false)))
                .thenReturn(mockRoundRobinPreTestingView);

        // Call the method
        CurrentBracket_SAC_PDF currentBracketSacPdf = new CurrentBracket_SAC_PDF();
        currentBracketSacPdf.displayRoundRobinPre(mockBracketContainer);

        // Verify that LayoutInflater.inflate was called with the correct parameters
        verify(mockInflater).inflate(eq(R.layout.round_robin_pre_testing), (ViewGroup) any(ViewGroup.class), eq(false));

        // Verify that the inflated view is added to the bracketContainer
        verify(mockBracketContainer).addView(mockRoundRobinPreTestingView);
    }


    @Test
    public void testDisplaySwissStage_8Players() {
        // Mock LinearLayout and LayoutInflater
        LinearLayout mockBracketContainer = mock(LinearLayout.class);
        LayoutInflater mockInflater = mock(LayoutInflater.class);

        // Mock inflated view for 8 players
        View mockSwissStagePreview8 = mock(View.class);
        when(mockInflater.inflate(eq(R.layout.swiss_pool_display_8p), (ViewGroup) any(ViewGroup.class), eq(false)))
                .thenReturn(mockSwissStagePreview8);

        // Call the method with 8 players
        CurrentBracket_SAC_PDF currentBracketSacPdf = new CurrentBracket_SAC_PDF();
        currentBracketSacPdf.numberOfPlayers = 8;
        currentBracketSacPdf.displaySwissStage(mockBracketContainer);

        // Verify that LayoutInflater.inflate was called with the correct parameters
        verify(mockInflater).inflate(eq(R.layout.swiss_pool_display_8p), (ViewGroup) any(ViewGroup.class), eq(false));

        // Verify that the inflated view is added to the bracketContainer
        verify(mockBracketContainer).addView(mockSwissStagePreview8);
    }


    @Test
    public void testDisplaySwissStage_16Players() {
        // Mock LinearLayout and LayoutInflater
        LinearLayout mockBracketContainer = mock(LinearLayout.class);
        LayoutInflater mockInflater = mock(LayoutInflater.class);

        // Mock inflated view for 16 players
        View mockSwissStagePreview16 = mock(View.class);
        when(mockInflater.inflate(eq(R.layout.swiss_pool_display_16p), (ViewGroup) any(ViewGroup.class), eq(false)))
                .thenReturn(mockSwissStagePreview16);

        // Call the method with 16 players
        CurrentBracket_SAC_PDF currentBracketSacPdf = new CurrentBracket_SAC_PDF();
        currentBracketSacPdf.numberOfPlayers = 16;
        currentBracketSacPdf.displaySwissStage(mockBracketContainer);

        // Verify that LayoutInflater.inflate was called with the correct parameters
        verify(mockInflater).inflate(eq(R.layout.swiss_pool_display_16p), (ViewGroup) any(ViewGroup.class), eq(false));

        // Verify that the inflated view is added to the bracketContainer
        verify(mockBracketContainer).addView(mockSwissStagePreview16);
    }
}
