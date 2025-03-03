package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
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
        return new CreateGameResponse(newGame.id(), newGame.gameName(), 200);
    }

    public GetGameResponse getGames(String authToken) throws DataAccessException{
        getAuthorization(authToken);
        Collection<GameData> games = dataAccess.listGames();
        return new GetGameResponse(games, 200);
    }

    public UpdateGameResponse updateGame(UpdateGameRequest request) throws DataAccessException{
        getAuthorization(request.authToken());
        dataAccess.updateGame(request.gameId(), request.authToken(), request.playerColor());
        return new UpdateGameResponse("Added player to game!");
    }

    public void getAuthorization(String authToken) throws DataAccessException{
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException(401, "Not authorized");
        }
    }

}
