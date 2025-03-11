package results;

public record UpdateGameRequest(Integer gameID, String playerColor, String authToken) {
}
