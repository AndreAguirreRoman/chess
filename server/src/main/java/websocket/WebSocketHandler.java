package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCmd;
import websocket.commands.UserGameCommand;
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
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCmd moveAction = new Gson().fromJson(message, MakeMoveCmd.class);
        switch (action.getCommandType()) {
            case CONNECT -> enter(action, session);
            case LEAVE -> exit(action, session);
            case RESIGN -> resign(action, session);
            case MAKE_MOVE -> make_move(moveAction, moveAction.getChessMove(), session);
        }
    }

    private void exit(UserGameCommand action, Session session) throws IOException, DataAccessException {
        String username = userService.getUserByAuth(action.getAuthToken()).username();
        connections.broadcast(username, new Notification(username + " , left the game!"), action.getGameID());
        connections.remove(username, session, action.getGameID());

    }

    private void enter(UserGameCommand action, Session session) throws IOException, DataAccessException {
        String username = userService.getUserByAuth(action.getAuthToken()).username();
        GameData game = gameService.getGame(action.getGameID());
        String team = "";
        if (game.whiteUsername() != null && game.whiteUsername().equals(username)){
            team = " as white team";
        }
        else if (game.blackUsername() != null && game.blackUsername().equals(username)){
            team = " as black team";
        } else {
            team = " as an observer";
        }
        connections.add(username, session, action.getGameID());
        connections.broadcast(username, new Notification(username + " , Joined the game," + team), action.getGameID());
    }

    private void resign(UserGameCommand action, Session session) throws IOException, DataAccessException {
        String username = userService.getUserByAuth(action.getAuthToken()).username();
        connections.broadcast(username, new Notification(username + " , Resigned the game :( "), action.getGameID());
    }

    private void make_move(MakeMoveCmd username, ChessMove chessMove, Session session) throws IOException {

    }



}



