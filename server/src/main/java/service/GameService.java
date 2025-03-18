package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import results.*;

import java.util.Collection;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    
    public CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException {
        getAuthorization(request.authToken());
        GameData newGame = dataAccess.createGame(new GameData(0, null, null, request.gameName(), null));

        return new CreateGameResponse(newGame.gameID());
    }


    public GetGameResponse getGames(String authToken) throws DataAccessException{
        getAuthorization(authToken);
        Collection<GameData> games = dataAccess.getGames();
        return new GetGameResponse(games, 200);
    }


    public UpdateGameResponse updateGame(UpdateGameRequest request) throws DataAccessException{

        getAuthorization(request.authToken());

        if (request.playerColor() == null || request.gameID() == null){
            throw new DataAccessException("Error bad request");
        }
        String teamColor = request.playerColor();
        GameData game = dataAccess.getGame(request.gameID());


        if (!teamColor.equalsIgnoreCase("WHITE") && !teamColor.equalsIgnoreCase("BLACK")) {
            throw new DataAccessException("Error bad request");
        }
        if (teamColor.equalsIgnoreCase("WHITE")){
            if (game.whiteUsername() != null){
                throw new DataAccessException("Error already taken");
            }
        } else {
            if (game.blackUsername() != null){
                throw new DataAccessException("Error already taken");
            }
        }
        dataAccess.updateGame(request.gameID(), request.authToken(), request.playerColor());
        return new UpdateGameResponse(200);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return dataAccess.getGame(gameID);
    }

    private void getAuthorization(String authToken) throws DataAccessException{
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException("Get auth = Error unauthorized");
        }
    }

}
