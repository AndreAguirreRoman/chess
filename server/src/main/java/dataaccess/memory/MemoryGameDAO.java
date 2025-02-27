package dataaccess.memory;

import chess.ChessGame;
import dataaccess.DAO.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> gamesList = new HashMap<>();
    private int nexId = 1;

    public GameData createGame(GameData gameData) throws DataAccessException {
        if (gamesList.containsKey(gameData.id())) {
            throw new DataAccessException("Game already exists");
        }
        gameData = new GameData(nexId++, gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), gameData.game());
        gamesList.put(gameData.id(), gameData);
        return gameData;
    }

    public GameData getGame(int gameId) throws DataAccessException{
        if (!gamesList.containsKey(gameId)){
            throw new DataAccessException("Not such game");
        }
        return gamesList.get(gameId);
    }

    public Collection<GameData> listGames() {
        return gamesList.values();
    }

    public void updateGameName(int gameId, String gameName) throws DataAccessException{
        if (!gamesList.containsKey(gameId)){
            throw new DataAccessException("no game to udpate:(");
        } else {
            GameData oldGame = gamesList.get(gameId);
            GameData updatedGame = new GameData(gameId, oldGame.whiteUsername(),
                    oldGame.blackUsername(), gameName, oldGame.game());
            gamesList.put(gameId, updatedGame);
        }
    }

    public void updateGame(int gameId, GameData newGameData){
        gamesList.put(gameId, newGameData);
    }
}
