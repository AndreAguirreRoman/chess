package results;

import chess.ChessGame;

public record UpdateGameRequest(Integer gameID, String playerColor, String authToken, String chessGame, String gameOver, boolean leaving) {
}
