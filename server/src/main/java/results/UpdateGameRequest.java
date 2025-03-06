package results;

public record UpdateGameRequest(int gameId, String authToken, String playerColor) {
}
