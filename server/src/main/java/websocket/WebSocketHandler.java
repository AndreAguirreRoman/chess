package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
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
            case CONNECT -> enter(action.getAuthToken(), session);
            case LEAVE -> exit(action.getAuthToken(), session);
            case RESIGN -> resign(action.getAuthToken(), session);
            case MAKE_MOVE -> make_move(moveAction.getAuthToken(), moveAction.getChessMove(), session);
        }
    }

    private void exit(String authToken, Session session) throws IOException, DataAccessException {
        String username = userService.getUserByAuth(authToken).username();
        connections.broadcast(username, new Notification(username + " , left the game!"));
        connections.remove(username);

    }

    private void enter(String authToken, Session session) throws IOException, DataAccessException {
        String username = userService.getUserByAuth(authToken).username();
        connections.add(username, session);
        connections.broadcast(username, new Notification(username + " , Joined the game!"));
    }

    private void resign(String authToken, Session session) throws IOException, DataAccessException {
        String username = userService.getUserByAuth(authToken).username();
        connections.broadcast(username, new Notification(username + " , Resigned the game :( "));
    }

    private void make_move(String username, ChessMove chessMove, Session session) throws IOException {

    }



}



