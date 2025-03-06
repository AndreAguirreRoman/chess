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
        System.out.println(" Game created with IDDDD: " + newGame.gameId());

        return new CreateGameResponse(newGame.gameId());
    }


    public GetGameResponse getGames(String authToken) throws DataAccessException{
        getAuthorization(authToken);
        Collection<GameData> games = dataAccess.listGames();
        return new GetGameResponse(games, 200);
    }


    public UpdateGameResponse updateGame(UpdateGameRequest request) throws DataAccessException{
        getAuthorization(request.authToken());
        System.out.println("REQUESTTTT " + request);
        String teamColor = request.playerColor();

        GameData game = dataAccess.getGame(request.gameId());
        System.out.println("GAMEEEE" + game);
        if (!teamColor.equalsIgnoreCase("WHITE") && !teamColor.equalsIgnoreCase("BLACK")) {
            throw new DataAccessException(400, "Error no team selected");
        }
        if (teamColor.equalsIgnoreCase("WHITE")){
            if (game.whiteUsername() != null){
                throw new DataAccessException(403, "already taken");
            }
        } else {
            if (game.blackUsername() != null){
                throw new DataAccessException(403, "already taken");
            }
        }
        dataAccess.updateGame(request.gameId(), request.authToken(), request.playerColor());
        return new UpdateGameResponse(200);
    }

    public void getAuthorization(String authToken) throws DataAccessException{
        if (dataAccess.getAuth(authToken) == null){
            System.out.println("Error in authToken");
            throw new DataAccessException(401, "unauthorized");
        }
    }

}
