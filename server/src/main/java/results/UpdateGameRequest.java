package results;

public class UpdateGameRequest {
    private Integer gameID;
    private String playerColor;
    private String authToken;

    public UpdateGameRequest(Integer gameID, String playerColor, String authToken) {
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.authToken = authToken;
    }

    public Integer getGameId() { return gameID; }
    public String getPlayerColor() { return playerColor; }
    public String getAuthToken() { return authToken; }

    @Override
    public String toString() {
        return "UpdateGameRequest{" +
                "gameId=" + gameID +
                ", playerColor='" + playerColor + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
