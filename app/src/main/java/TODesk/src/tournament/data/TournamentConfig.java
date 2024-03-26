package TODesk.src.tournament.data;

public class TournamentConfig {
    String TournamentName, Game;
    Integer Bracket;

    //Bracket should set Robin to 0, Swiss to 1, DoubleElim to 2
    enum Format {
        RR,
        Swiss,
        DoubleElim,
    }
    public TournamentConfig(){

    }

    public TournamentConfig(String tournamentName, String game, Integer bracket) {
        TournamentName = tournamentName;
        Game = game;
        Bracket = bracket;
    }
//get+set methods for TournamentName, Game, Bracket
    public String getTournamentName() {
        return TournamentName;
    }

    public void setTournamentName(String tournamentName) {
        TournamentName = tournamentName;
    }

    public String getGame() {
        return Game;
    }

    public void setGame(String game) {
        Game = game;
    }

    public Integer getBracket() {
        return Bracket;
    }

    public void setBracket(Integer bracket) {
        Bracket = bracket;
    }
}
