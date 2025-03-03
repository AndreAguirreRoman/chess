package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<Integer, GameData> gamesList = new HashMap<>();
    private final HashMap<String, UserData> usersList = new HashMap<>();
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    private int nextId = 1;

    public UserData createUser(UserData user) throws DataAccessException {
        if (usersList.containsKey(user.username())) {
            throw new DataAccessException("User Already Exists!");
        }
        user = new UserData(nextId++, user.username(), user.email(), user.password());
        usersList.put(user.username(), user);
        return user;
    }

    public UserData getUser(String username){
        return usersList.get(username);
    }

    public void deleteUser(String username) throws DataAccessException{
        if (!usersList.containsKey(username)){
            throw new DataAccessException("User not found!");
        }
        usersList.remove(username);
    }

    public void clearUserList(){
        usersList.clear();
    }


    public GameData createGame(GameData gameData) throws DataAccessException {
        if (gamesList.containsKey(gameData.id())) {
            throw new DataAccessException("Game already exists");
        }
        gameData = new GameData(nextId++, gameData.whiteUsername(),
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


    public AuthData createAuth(UserData userData, String authToken) throws DataAccessException{
        for (AuthData existingToken : authTokens.values()){
            if (existingToken.userName().equals(userData.username())) {
                return existingToken;
            }
        }
        AuthData authData = new AuthData(generateToken(), userData.username());
        authTokens.put(authData.authToken(), authData);
        return authData;
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("No authorization!");
        }
        return authTokens.get(authToken);
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("No auth found!");
        }
        authTokens.remove(authToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void clear(){
        gamesList.clear();
        usersList.clear();
        authTokens.clear();
    }
}
