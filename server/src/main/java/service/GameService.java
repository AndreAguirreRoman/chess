package service;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
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
        ChessGame game = new ChessGame();
        GameData newGame = dataAccess.createGame(new GameData(0, null, null, request.gameName(), game, "false"));

        return new CreateGameResponse(newGame.gameID());
    }


    public GetGameResponse getGames(String authToken) throws DataAccessException{
        getAuthorization(authToken);
        Collection<GameData> games = dataAccess.getGames();
        return new GetGameResponse(games, 200);
    }


    public UpdateGameResponse updateGame(UpdateGameRequest request) throws DataAccessException{

        getAuthorization(request.authToken());
        boolean leave = request.leaving();
        if (request.chessGame() != null && request.playerColor() == null){
            dataAccess.updateBoard(request.gameID(), request.chessGame(), request.gameOver());
        } else if (request.playerColor() == null || request.gameID() == null){
            throw new DataAccessException(400, "Error bad in Update request");
        } else {
            String teamColor = request.playerColor();
            GameData game = dataAccess.getGame(request.gameID());


            if (!teamColor.equalsIgnoreCase("WHITE") && !teamColor.equalsIgnoreCase("BLACK")) {
                throw new DataAccessException(400, "Error bad request in games");
            }
            String username = dataAccess.getAuth(request.authToken()).userName();
            if (leave) {
                if (teamColor.equalsIgnoreCase("WHITE")) {
                    if (username.equals(game.whiteUsername())) {
                        dataAccess.updateGame(request.gameID(), null, "WHITE");
                    } else {
                        throw new DataAccessException(403, "You are not the current white player");
                    }
                } else {
                    if (username.equals(game.blackUsername())) {
                        dataAccess.updateGame(request.gameID(), null, "BLACK");
                    } else {
                        throw new DataAccessException(403, "You are not the current black player");
                    }
                }
            } else {
                if (teamColor.equalsIgnoreCase("WHITE")) {
                    if (game.whiteUsername() != null) {
                        throw new DataAccessException(403, "Error already taken");
                    }
                } else {
                    if (game.blackUsername() != null) {
                        throw new DataAccessException(403, "Error already taken");
                    }
                }
                dataAccess.updateGame(request.gameID(), username, request.playerColor());
            }
        }

        return new UpdateGameResponse(200);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        System.out.println("Fetching game for ID: " + gameID);
        GameData game = dataAccess.getGame(gameID);
        if (game == null){
            throw new DataAccessException(500, "Error bad request in get games");
        }

        return dataAccess.getGame(gameID);
    }

    private void getAuthorization(String authToken) throws DataAccessException{
        if (dataAccess.getAuth(authToken) == null){
            throw new DataAccessException(401, "Get auth = Error unauthorized");
        }
    }

}
