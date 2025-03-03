package results;

import model.GameData;

public record UpdateGameRequest(int gameId, String authToken, String playerColor) {
}
