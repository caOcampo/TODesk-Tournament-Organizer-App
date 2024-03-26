package TODesk.src.tournament.data;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TournamentEntry {
    //initializing player list with player entries
    public List<PlayerEntry> PlayerList;

    //might not need the tournament config to be in a list (idk) - R
    public List<TournamentConfig> TournamentData;

    //new PlayerList
    public TournamentEntry(){
        this.PlayerList = new ArrayList<>();
        this.TournamentData = new ArrayList<>();
    }

    public void addPlayer(PlayerEntry player){
        PlayerList.add(player);
    }

    public void removePlayer(PlayerEntry player){
        PlayerList.removeIf(i -> i == player);
    }

    //used for the removePlayer method
    public PlayerEntry findPlayer(PlayerEntry player){
        for (PlayerEntry i : PlayerList) {
            if (i == player) {
                return i;
            }
        }
        return null;
    }

    public void editPlayer(){

    }

    //loads the PlayerList for display
    public void loadPlayerList(TournamentEntry tData){

    }

    public int countPlayer(){
        return PlayerList.size();
    }

    public void tournamentUpdate(){

    }

    public List<PlayerEntry> getPlayerList(){
        return PlayerList;
    }

    public void readCSV(){
        String csvFile = "C:\\Users\\Calvi\\OneDrive\\Vintage\\GitHub\\CS401-Group-6-Project-2\\TODesk\\src\\testplayers.csv";
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                // Assuming the CSV format is: username,organization,rank
                if (fields.length >= 3) {
                    String username = fields[0];
                    String organization = fields[1];
                    String rank = fields[2];

                    PlayerEntry playerEntry = new PlayerEntry(username, organization, rank);
                    PlayerList.add(playerEntry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printPlayers(){
        for (PlayerEntry entry : PlayerList) {
            System.out.println(entry);
        }
    }



    //Return the winner of the match
    /*public PlayerEntry getWinner(){

    }*/
}
