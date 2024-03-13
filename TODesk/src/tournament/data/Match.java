package tournament.data;
import tournament.data.Player;

public class Match {
    Player player1;
    int player1score;
    Player player2;
    int player2score;
    boolean complete;

    public Match(Player firstPlayer, Player secondPlayer){
        player1 = firstPlayer;
        player2 = secondPlayer;
        int player1score = 0;
        int player2score=0;
    }

    //Return the winner of the match
    public Player getWinner(){

    }
}
