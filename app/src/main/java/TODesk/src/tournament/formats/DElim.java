package tournament.formats;
import tournament.data.PlayerEntry;
import tournament.data.TournamentConfig;
import tournament.data.TournamentEntry;

import java.util.ArrayList;

public class DElim {
    private ArrayList<Match> upperBracket;
    private ArrayList<Match> lowerBracket;

    private static DElim instance = null;

    private DElim() {
        upperBracket = new ArrayList<>();
        lowerBracket = new ArrayList<>();
    }

    public static DElim getInstance() {
        if (instance == null) {
            instance = new DElim();
        }
        return instance;
    }

    public void createUpperBracket(ArrayList<PlayerEntry> playerEntries) {
        int size = playerEntries.size();
        for (int i = 0; i < size; i += 2) {
            Match match = new Match();
            match.setPlayer1(playerEntries.get(i));
            match.setPlayer2(playerEntries.get(i + 1));
            upperBracket.add(match);
        }
    }

    public void updateMatchScore(int matchIndex, int player1Score, int player2Score) {
        if (matchIndex >= 0 && matchIndex < upperBracket.size()) {
            Match match = upperBracket.get(matchIndex);
            match.setPlayer1Score(player1Score);
            match.setPlayer2Score(player2Score);
        } else {
            System.out.println("Invalid match index");
        }
    }

    public void advancePlayer(int matchIndex, PlayerEntry winner) {
        if (upperBracket.getLast().player2 == null){
            upperBracket.getLast().player2 = upperBracket.get(matchIndex).getWinner();
        }
        else {
            Match nextMatch = new Match();
            nextMatch.player1 =  upperBracket.get(matchIndex).getWinner();
            upperBracket.add(nextMatch);
        }

        if (lowerBracket.getLast().player2 == null) {
            lowerBracket.getLast().player2 = upperBracket.get(matchIndex).getLoser();
        }
        else {
            lowerBracket.add(new Match(upperBracket.get(matchIndex).getLoser(), null)); // Send loser to lower bracket
        }

    }

    public void displayUpperBracket() {
        System.out.println("Upper Bracket:");
        for (Match match : upperBracket) {
            System.out.println(match.getPlayer1().getUsername() + " vs " + match.getPlayer2().getUsername());
        }
    }

    public void displayLowerBracket() {
        System.out.println("Lower Bracket:");
        for (Match match : lowerBracket) {
            System.out.println(match.getPlayer1().getUsername() + " vs " + match.getPlayer2().getUsername());
        }
    }

    //Match, an individual match of the tournament
    public class Match {
        PlayerEntry player1;
        PlayerEntry player2;
        int player1Score;
        int player2Score;

        public Match(){
            player1 = null;
            player2 = null;
            player1Score = 0;
            player2Score = 0;
        }
        public Match(PlayerEntry player1, PlayerEntry player2){
            this.player1 = player1;
            this.player2 = player2;
            player1Score = 0;
            player2Score = 0;
        }

        public void setPlayer1(PlayerEntry player1) {
            this.player1 = player1;
        }

        public void setPlayer2(PlayerEntry player2) {
            this.player2 = player2;
        }

        public void incrementPlayer1Score(){
            player1Score++;
        }
        public void incrementPlayer2Score(){
            player2Score++;
        }

        public void setPlayer1Score(int score){
            player1Score = score;
        }
        public void setPlayer2Score(int score){
            player2Score = score;
        }

        public PlayerEntry getPlayer1() {
            return player1;
        }
        public PlayerEntry getPlayer2() {
            return player2;
        }


        PlayerEntry getWinner(){
            if (player1Score == player2Score) {
                return null;
            }
            else if (player1Score > player2Score){
                return player1;
            }
            else {
                return player2;
            }
        }
        PlayerEntry getLoser(){
            if (player1Score == player2Score) {
                return null;
            }
            else if (player1Score < player2Score){
                return player1;
            }
            else {
                return player2;
            }
        }
    }
}
