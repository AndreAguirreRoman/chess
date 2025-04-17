package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    AuthData createAuth(UserData userData, String authToken) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    default void deleteAuth(String authToken) throws DataAccessException {}
    GameData createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;
    Collection<GameData> getGames();
    void updateGame(int gameId, String authToken, String playerColor) throws DataAccessException;
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
    AuthData getUserByAuth(String authToken);
    void clear() throws  DataAccessException;
    void updateBoard(int gameID, String chessGame, String gameOver) throws DataAccessException;
}
