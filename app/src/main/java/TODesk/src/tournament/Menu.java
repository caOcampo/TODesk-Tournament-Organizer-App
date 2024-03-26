package TODesk.src.tournament;

import java.util.*;

//import android packages to interact with android studio

import android.os.Bundle;    //passes data between activities
import android.view.View;    //GUI components
import android.widget.Button;     //button functionality
import android.widget.Toast;     //displays a message
import androidx.appcompat.app.AppCompatActivity;     //android version compatability


public class Menu {


    /* Menu prompts, but they are mostly buttons so I have to check how the
    user interaction will go and how to code.

    Buttons:
    NewTournament
    ViewTournament

    InitializeAddPlayer
    AddPlayer
    SaveTournamentConfig
    InitializeTournamentBracket

    TournamentBracketSave
    TournamentCodeGenerate

    TournamentCodeSubmit
    EditTournament
    AccessCodeSubmit
    ViewTournament

    Home
    BackButton
    SaveToPDF
    * */
    protected void mainMenu(Bundle savedInstanceState){
        //maintains the previous state of this function is the screen is rotated
        super.mainMenu(savedInstanceState);

        //displays the MainMenu GUI
        setContentView(R.layout./*XML file for mainMenu*/);

        // creating a button for CreatingNewTournament
        Button CNTButton = findViewById(R.id.CNTButton);

        //performs this function when button is clicked
        CNTButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Function to perform when the button is clicked
                ConfigureTournament();
            }
        });

        //creating button for ViewExistingTournaments
        Button VETButton = findViewById(R.id.VETButton);
        VETButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Function to perform when the button is clicked
                EnterTournamentCode();
            }
        });
    }

}
