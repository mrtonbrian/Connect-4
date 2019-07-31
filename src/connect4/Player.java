package connect4;

public class Player {
    String playerName;
    int playerNum;

    Player(String playerName, int playerNum) {
        this.playerName = playerName;
        this.playerNum = playerNum;
    }

    public String getName() {
        return playerName;
    }

    public int getPlayerNum() {
        return playerNum;
    }
}