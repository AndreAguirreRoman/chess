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
            throw new DataAccessException(401, "User Already Exists!");
        }
        user = new UserData(nextUserId++, user.username(), user.email(), user.password());
        usersList.put(user.username(), user);
        return user;
    }

    public UserData getUser(String username){
        return usersList.get(username);
    }


    public GameData createGame(GameData gameData) throws DataAccessException {
        int newGameId = nextGameId++;
        if (gamesList.containsKey(newGameId)) {
            throw new DataAccessException(400,"bad request");
        }
        gameData = new GameData(newGameId, gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), gameData.game());

        gamesList.put(newGameId, gameData);
        System.out.println("List of games: " + getGames());
        return gameData;
    }

    public GameData getGame(int gameId) throws DataAccessException{

        if (!gamesList.containsKey(gameId)){
            throw new DataAccessException(400, "Error Not such game");
        }
        GameData game = gamesList.get(gameId);
        return game;
    }

    public Collection<GameData> getGames() {
        return gamesList.values();
    }

    public void updateGame(int gameId, String authToken, String playerColor) throws DataAccessException{
        if (authTokens.containsKey(authToken)){
            GameData game = getGame(gameId);

            if (game == null){
                throw new DataAccessException(400, "Error Game not found ");
            }
            String userName = authTokens.get(authToken).userName();
            GameData newGameData = null;
            if (playerColor.equalsIgnoreCase("WHITE")){
                if (game.whiteUsername() != null) {
                    throw new DataAccessException(403, "Error White slot taken ");
                }
                newGameData = new GameData(game.gameID(), userName,
                        game.blackUsername(),game.gameName(), game.game());
            } else if (playerColor.equalsIgnoreCase("BLACK")){
                if (game.blackUsername() != null) {
                    throw new DataAccessException(403, "Error Black slot taken");
                }
                 newGameData = new GameData(game.gameID(), game.whiteUsername(),
                        userName,game.gameName(), game.game());
            }
            System.out.println("List of games after update: " + getGames());

            gamesList.put(gameId, newGameData);
        }

    }


    public AuthData createAuth(UserData userData, String authToken) throws DataAccessException{
        AuthData authData = new AuthData(authToken, userData.username());
        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            System.out.println(authTokens);
            throw new DataAccessException(401, "Error: unauthorized");
        }
        return authTokens.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException(400, "Error No auth found!");
        }
        authTokens.remove(authToken);
    }


    public void clear(){
        gamesList.clear();
        usersList.clear();
        authTokens.clear();
    }
}
