package toDesk.src.tournament.formats;
import toDesk.src.tournament.data.PlayerEntry;

import java.util.*;

/**
        *
        * @author Calvin Ocampo
        * @version 1.0
        * @since  4/01/2024
        *
        * purpose: This class simulates a double elimination bracket. Each match contains 2 players, and every player starts on the upper bracket. Winners advance through the
        * upper bracket, and losers are sent to the lower bracket.
*/
public class DElim {
    /**
     * Upper bracket, contains all starting players, then all winning players
     */
    private ArrayList<Match> upperBracket;

    /**
     * Lower bracket contains all players who lost in the upper bracket, and those who lose in this bracket are eliminated.
     */
    private ArrayList<Match> lowerBracket;

    private static DElim instance = null;

    /**
     * Default constructor. Initializes brackets to be empty ArrayList objects.
     */
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
    /**
    * Creates the upper bracket
    * @param playerEntries is an existing list of players
     */
    public void createUpperBracket(ArrayList<PlayerEntry> playerEntries) {
        int size = playerEntries.size();
        for (int i = 0; i < size; i += 2) {
            Match match = new Match();
            match.setPlayer1(playerEntries.get(i));
            match.setPlayer2(playerEntries.get(i + 1));
            upperBracket.add(match);
        }
    }
    /**
     * Updates the score of a match object
     * @param matchIndex the index of a match in the bracket
     * @param player1Score is the score of the 1st player
     * @param player2Score is the score of the 2nd player
    */
    public void updateMatchScore(int matchIndex, int player1Score, int player2Score) {
        if (matchIndex >= 0 && matchIndex < upperBracket.size()) {
            Match match = upperBracket.get(matchIndex);
            match.setPlayer1Score(player1Score);
            match.setPlayer2Score(player2Score);
        } else {
            System.out.println("Invalid match index");
        }
    }

    /**
     * Advances a player through the bracket by sending the winning player to the next available match in the list
     * and the losing player to the lower bracket OR eliminating them
     * @param matchIndex is the index of the match to be modified
     * @param winner is the player who won the match
     */
    public void advancePlayer(int matchIndex, PlayerEntry winner) {
        if (upperBracket.get(lowerBracket.size()-1).player2 == null){
            upperBracket.get(lowerBracket.size()-1).player2 = upperBracket.get(matchIndex).getWinner();
        }
        else {
            Match nextMatch = new Match();
            nextMatch.player1 =  upperBracket.get(matchIndex).getWinner();
            upperBracket.add(nextMatch);
        }

        if (lowerBracket.get(lowerBracket.size()-1).player2 == null) {
            lowerBracket.get(lowerBracket.size()-1).player2 = upperBracket.get(matchIndex).getLoser();
        }
        else {
            lowerBracket.add(new Match(upperBracket.get(matchIndex).getLoser(), null)); // Send loser to lower bracket
        }

    }

    /**
     * Prints the upper bracket.
     */
    public void displayUpperBracket() {
        System.out.println("Upper Bracket:");
        for (Match match : upperBracket) {
            System.out.println(match.getPlayer1().getUsername() + " vs " + match.getPlayer2().getUsername());
        }
    }

    /**
     * Prints the lower bracket.
     */
    public void displayLowerBracket() {
        System.out.println("Lower Bracket:");
        for (Match match : lowerBracket) {
            System.out.println(match.getPlayer1().getUsername() + " vs " + match.getPlayer2().getUsername());
        }
    }

    /**
     * Purpose: This class represents an individual match.
     */
    public class Match {
        /**
         * The first player in the match.
         */
        PlayerEntry player1;
        /**
         * The second player in the match.
         */
        PlayerEntry player2;
        /**
         * The score of the first player.
         */
        int player1Score;
        /**
         * The score of the second player.
         */
        int player2Score;

        /**
         * Returns a new new Match with no players and score 0-0.
         */
        public Match(){
            player1 = null;
            player2 = null;
            player1Score = 0;
            player2Score = 0;
        }

        /**
         * Returns a new match with players and a score of 0-0.
         * @param player1 is the first player.
         * @param player2 is the second player.
         */
        public Match(PlayerEntry player1, PlayerEntry player2){
            this.player1 = player1;
            this.player2 = player2;
            player1Score = 0;
            player2Score = 0;
        }

        /**
         * Manually sets the first player.
         * @param player1 is the first player.
         */
        public void setPlayer1(PlayerEntry player1) {
            this.player1 = player1;
        }

        /**
         * Manually sets the second player
         * @param player2 is the second player
         */
        public void setPlayer2(PlayerEntry player2) {
            this.player2 = player2;
        }

        /**
         * This function increments the 1st player's score
         */
        public void incrementPlayer1Score(){
            player1Score++;
        }
        /**
         * This function increments the 2nd player's score
         */
        public void incrementPlayer2Score(){
            player2Score++;
        }

        /**
         * This function manually sets the first player's score.
         * @param score is the desired score of the first player.
         */
        public void setPlayer1Score(int score){
            player1Score = score;
        }
        /**
         * This function manually sets the second player's score.
         * @param score is the desired score of the second player.
         */
        public void setPlayer2Score(int score){
            player2Score = score;
        }

        /**
         * Returns player 1.
         * @return The first player.
         */
        public PlayerEntry getPlayer1() {
            return player1;
        }

        /**
         * Returns player 2.
         * @return The second player.
         */
        public PlayerEntry getPlayer2() {
            return player2;
        }

        /**
         * Calculates the winning player based on which player has the largest score.
         * @return The player with the largest score.
         */
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

        /**
         * Calculates the player who lost based on who scored lowest.
         * @return The player with the lowest score.
         */
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
