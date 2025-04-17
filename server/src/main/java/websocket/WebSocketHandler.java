package websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import results.UpdateGameRequest;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCmd;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    MySqlDataAccess sql;
    GameService gameService;
    UserService userService;

    private final GameService service = new GameService(sql);

    public WebSocketHandler() throws DataAccessException {
        try {
            this.sql = new MySqlDataAccess();
            this.gameService = new GameService(sql);
            this.userService = new UserService(sql);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        try{
            UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> enter(action, session);
                case LEAVE -> exit(action, session);
                case RESIGN -> resign(action, session);
                case MAKE_MOVE -> {
                    MakeMoveCmd moveAction = new Gson().fromJson(message, MakeMoveCmd.class);
                    make_move(moveAction, moveAction.getMove(), session);
                }
            }
        } catch (Exception e) {
            session.getRemote().sendString(new Gson().toJson(new Error("Invalid authToken for move" + e.getMessage())));
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) throws IOException {
        error.printStackTrace();
        session.getRemote().sendString("Error: " + error.getMessage());
    }

    private void exit(UserGameCommand action, Session session) throws IOException, DataAccessException {
        String username = userService.getUserByAuth(action.getAuthToken()).username();
        GameData game = gameService.getGame(action.getGameID());
        String teamColor = null;
        if (username.equals(game.whiteUsername())){
            teamColor = "white";
        } else if (username.equals(game.blackUsername())) {
            teamColor ="black";
        }

        if (teamColor != null){
            UpdateGameRequest leaveGame = new UpdateGameRequest(action.getGameID(), teamColor, action.getAuthToken(), null, "false",true);
            gameService.updateGame(leaveGame);
        }
        connections.broadcast(username, new Notification(username + " , left the game!"), action.getGameID());
        connections.remove(username, action.getGameID());

    }

    private void enter(UserGameCommand action, Session session) throws IOException, DataAccessException {
        String auth = action.getAuthToken();
        if (auth.length() < 10){
            session.getRemote().sendString(new Gson().toJson(new Error("Error: game not found")));
            return;
        }
        String username = userService.getUserByAuth(action.getAuthToken()).username();

        connections.add(username, session, action.getGameID());

        GameData game;
        try {
             game = gameService.getGame(action.getGameID());
        } catch (DataAccessException e) {
            connections.sendOneUser(username, new Error("NO GAME WITH SUCH ID"), action.getGameID());
            return;
        }

        String team = "";

        if (game.whiteUsername() != null && game.whiteUsername().equals(username)){
            team = " as white team";
        }
        else if (game.blackUsername() != null && game.blackUsername().equals(username)){
            team = " as black team";
        } else {
            team = " as an observer";
        }
        connections.sendOneUser(username, new LoadGame(game.game()), action.getGameID());
        connections.broadcast(username, new Notification(username + " , Joined the game," + team), action.getGameID());
    }

    private void resign(UserGameCommand action, Session session) throws IOException, DataAccessException {
        String username = userService.getUserByAuth(action.getAuthToken()).username();
        GameData game = gameService.getGame(action.getGameID());
        if (!game.blackUsername().equals(username) && !game.whiteUsername().equals(username)){
            session.getRemote().sendString(new Gson().toJson(new Error("You are an observer, can't resign!")));
            return;
        }
        if (game.gameOver() != null && game.gameOver().equals("true")) {
            session.getRemote().sendString(new Gson().toJson(new Error("Game is already over, cannot resign.")));
            return;
        }
        game.game().setGameOver(true);
        String gameJsonOver = new Gson().toJson(game);
        gameService.updateGame(new UpdateGameRequest(action.getGameID(), null,action.getAuthToken(), gameJsonOver, "true", true));
        connections.broadcast("", new Notification(username + " , Resigned the game :( "), action.getGameID());
    }

    private void make_move(MakeMoveCmd moveCmd, ChessMove chessMove, Session session) throws IOException, DataAccessException {
        if (chessMove == null) {
            System.out.println("ERROR IN MOVE!");
            return;
        }
        if (moveCmd.getAuthToken() == null) {
            var errorMessage = new Error("Invalid auth token");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }

        String auth = moveCmd.getAuthToken();
        int gameID = moveCmd.getGameID();
        String username = userService.getUserByAuth(auth).username();

        GameData gameData = gameService.getGame(gameID);
        ChessGame chessGame = gameData.game();
        if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) || chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            connections.sendOneUser(username,new Error("No more moves, game is over!"), gameID);
            return;
        }
        if (gameData.gameOver() != null && gameData.gameOver().equals("true")){
            connections.sendOneUser(username,new Error("No more moves, game is over!"), gameID);
            return;
        }

        ChessGame.TeamColor currentTurn = gameData.game().getTeamTurn();

        if ((currentTurn == ChessGame.TeamColor.WHITE && !username.equals(gameData.whiteUsername())) ||
                (currentTurn == ChessGame.TeamColor.BLACK && !username.equals(gameData.blackUsername()))){
           Error err = new Error("NOT YOUR TURN BUDDY");
           connections.sendOneUser(username, err, gameID);
           return;
        }


        try {
            gameData.game().makeMove(chessMove);
            var gameJson = new Gson().toJson(gameData.game());
            gameService.updateGame(new UpdateGameRequest(gameID, null, auth, gameJson,"false", false));
            connections.broadcast("", new LoadGame((gameData.game())), moveCmd.getGameID());
            connections.broadcast(username, new Notification((username + "made a move to:" + (moveCmd.getMove().getEndPosition()))), moveCmd.getGameID());

        } catch (InvalidMoveException e) {
            connections.sendOneUser(username, new Error("Error: bad move"), gameID);
        }
    }



}



