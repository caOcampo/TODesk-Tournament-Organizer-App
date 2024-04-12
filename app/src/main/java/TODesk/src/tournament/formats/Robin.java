package TODesk.src.tournament.formats;

import java.util.*;

public class RoundRobinTournament {
    static class Player {
        String username;
        String organization;
        String rank;
        int wins;
        int losses;

        public Player(String username, String organization, String rank) {
            this.username = username;
            this.organization = organization;
            this.rank = rank;
            this.wins = 0;
            this.losses = 0;
        }
    }

    public static void main(String[] args) {
        Player[] players = {
                new Player("Player1", "Org1", "Rank1"),
                new Player("Player2", "Org2", "Rank2"),
                new Player("Player3", "Org3", "Rank3"),
                new Player("Player4", "Org4", "Rank4"),
                new Player("Player5", "Org5", "Rank5"),
                new Player("Player6", "Org6", "Rank6"),
                new Player("Player7", "Org7", "Rank7"),
                new Player("Player8", "Org8", "Rank8")
        };

        Scanner scanner = new Scanner(System.in);

        // Round Robin Matches
        for (int i = 0; i < players.length; i++) {
            for (int j = i + 1; j < players.length; j++) {
                System.out.println("Match between " + players[i].username + " and " + players[j].username);
                System.out.print("Winner (Enter username): ");
                String winnerUsername = scanner.nextLine();

                Player winner = null;
                Player loser = null;

                if (winnerUsername.equals(players[i].username)) {
                    winner = players[i];
                    loser = players[j];
                } else if (winnerUsername.equals(players[j].username)) {
                    winner = players[j];
                    loser = players[i];
                } else {
                    System.out.println("Invalid username. Please enter one of the displayed usernames.");
                    j--; // Retry this match
                    continue;
                }

                winner.wins++;
                loser.losses++;
            }
        }

        // Sort players based on wins and losses
        Arrays.sort(players, Comparator.comparing((Player p) -> p.wins).reversed().thenComparing(p -> p.losses));

        // Display results
        System.out.println("\nTournament Results:");
        for (Player player : players) {
            System.out.println(player.username + " - Wins: " + player.wins + ", Losses: " + player.losses);
        }
    }
}
