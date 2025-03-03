package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    AuthData createAuth(UserData userData, String authToken) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    GameData createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGameName(int gameId, String gameName) throws DataAccessException;
    void updateGame(int gameId, GameData gameData);
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
    void deleteUser(String username) throws DataAccessException;
    void clearUserList();
    void clear();
}
