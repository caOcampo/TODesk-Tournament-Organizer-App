package toDesk.src.tournament.data;

import java.util.Scanner;

// Defining a class named Player to represent a player with certain attributes and behaviors

public class PlayerEntry {

    // Player Details
    private String username;
    private String organization;
    private String rank;
    private int wins;
    private int losses;

    // Constructor to initialize player's details
    public PlayerEntry(String username, String organization, String rank) {
        this.username = username;
        this.organization = organization;
        this.rank = rank;
        this.wins = 0; // Initializing wins to 0
        this.losses = 0; // Initializing losses to 0
    }

    // Getter method for retrieving username
    public String getUsername() {
        return username;
    }

    // Getter method for retrieving organization
    public String getOrganization() {
        return organization;
    }

    // Getter method for retrieving rank
    public String getRank() {
        return rank;
    }

    // Getter method for retrieving wins
    public int getWins() {
        return wins;
    }

    // Getter method for retrieving losses
    public int getLosses() {
        return losses;
    }

    // Method to increment the number of wins for a player
    public void incrementWins() {
        this.wins++;
    }

    // Method to increment the number of losses for a player
    public void incrementLosses() {
        this.losses++;
    }

    // Overriding toString() method to provide custom string representation of Player object
    @Override
    public String toString() {
        return "username/organization/rank:" + username + " /" + organization + " /" + rank;
    }

    // Main method - entry point of the program
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PlayerEntry[] players = new PlayerEntry[16]; // Creating an array to store player objects

        // Loop to input details for 16 players
        for (int i = 0; i < 16; i++) {
            System.out.println("Enter details for player " + (i + 1) + ":");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Organization: ");
            String organization = scanner.nextLine();
            System.out.print("Rank: ");
            String rank = scanner.nextLine();

            players[i] = new PlayerEntry(username, organization, rank); // Creating Player object and storing in array
        }

        System.out.println("\nPlayers:"); // Printing players' details
        for (PlayerEntry player : players) {
            System.out.println(player); // Invokes toString() method of Player class for each player object
        }
    }
}
