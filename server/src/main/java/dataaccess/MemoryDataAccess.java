package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<Integer, GameData> gamesList = new HashMap<>();
    private final HashMap<String, UserData> usersList = new HashMap<>();
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    private int nextUserId = 1;
    private int nextGameId = 1;

    public UserData createUser(UserData user) throws DataAccessException {
        if (usersList.containsKey(user.username())) {
            throw new DataAccessException(404, "User Already Exists!");
        }
        user = new UserData(nextUserId++, user.username(), user.email(), user.password());
        usersList.put(user.username(), user);
        return user;
    }

    public UserData getUser(String username){
        return usersList.get(username);
    }

    public void deleteUser(String username) throws DataAccessException{
        if (!usersList.containsKey(username)){
            throw new DataAccessException(404, "User not found!");
        }
        usersList.remove(username);
    }

    public void clearUserList(){
        usersList.clear();
    }


    public GameData createGame(GameData gameData) throws DataAccessException {
        int newGameId = nextGameId++;
        if (gamesList.containsKey(gameData.id())) {
            throw new DataAccessException(400, "bad request");
        }
        gameData = new GameData(newGameId, gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), gameData.game());
        gamesList.put(gameData.id(), gameData);
        return gameData;
    }

    public GameData getGame(int gameId) throws DataAccessException{
        if (!gamesList.containsKey(gameId)){
            throw new DataAccessException(404, "Not such game");
        }
        return gamesList.get(gameId);
    }

    public Collection<GameData> listGames() {
        return gamesList.values();
    }


    public void updateGame(int gameId, String authToken, String playerColor) throws DataAccessException{
        if (authTokens.containsKey(authToken)){
            GameData game = getGame(gameId);
            if (game == null){
                throw new DataAccessException(404, "Game not found");
            }
            String userName = authTokens.get(authToken).userName();
            GameData newGameData = null;
            if (playerColor.equalsIgnoreCase("WHITE")){
                if (game.whiteUsername() != null) {
                    throw new DataAccessException(403, "White slot taken");
                }
                newGameData = new GameData(game.id(), userName,
                        game.blackUsername(),game.gameName(), game.game());
            } else if (playerColor.equalsIgnoreCase("BLACK")){
                if (game.blackUsername() != null) {
                    throw new DataAccessException(403, "Black slot taken");
                }
                 newGameData = new GameData(game.id(), game.whiteUsername(),
                        userName,game.gameName(), game.game());
            }
            gamesList.put(gameId, newGameData);
        }
    }



    public AuthData createAuth(UserData userData, String authToken) throws DataAccessException{
        for (AuthData existingToken : authTokens.values()){
            if (existingToken.userName().equals(userData.username())) {
                return existingToken;
            }
        }
        AuthData authData = new AuthData(authToken, userData.username());
        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    public AuthData findAuthWithUser(String username){
        for (AuthData existingToken : authTokens.values()){
            if (existingToken.userName().equals(username)) {
                System.out.println(existingToken.userName());
                return existingToken;
            }
        }
        return null;
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException(401, "No authorization!");
        }
        return authTokens.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException(404, "No auth found!");
        }
        authTokens.remove(authToken);
    }


    public void clear(){
        gamesList.clear();
        usersList.clear();
        authTokens.clear();
    }
}
