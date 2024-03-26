package TODesk.src.tournament.data;

public class PlayerEntry {
    String username, organization, rank;

    public PlayerEntry(String username, String organization, String rank) {
        this.username = username;
        this.organization = organization;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "PlayerEntry{" +
                "username='" + username + '\'' +
                ", organization='" + organization + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }

    //get+set methods for ^

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
