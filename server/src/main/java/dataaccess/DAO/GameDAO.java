package dataaccess.DAO;

import dataaccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGameName(int gameId, String gameName) throws DataAccessException;
    void updateGame(int gameId, GameData gameData);

}
