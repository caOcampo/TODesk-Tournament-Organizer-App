package tournament.data;
import java.util.*;

public class TournamentEntry {

    //initializing player list with player entries
    public List<PlayerEntry> PlayerList;

    //might not need the tournament config to be in a list (idk) - R
    public List<TournamentConfig> TournamentData;

    //new PlayerList
    public TournamentEntry(){
        this.PlayerList = new ArrayList<>();
    }

    //used for the removePlayer method
    public void findPlayer(PlayerEntry player){

    }

    public void removePlayer(PlayerEntry player){

    }

    //loads the PlayerList for display
    public void loadPlayerList(TournamentEntry tData){

    }
    PlayerEntry player;
    int player1score;
    PlayerEntry player2;
    int player2score;
    boolean complete;

    public TournamentEntry(PlayerEntry firstPlayer, PlayerEntry secondPlayer){
        player = firstPlayer;
        player2 = secondPlayer;
        int player1score = 0;
        int player2score=0;
    }

    //Return the winner of the match
    /*public PlayerEntry getWinner(){

    }*/
}
