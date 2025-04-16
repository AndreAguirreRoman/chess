package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;

import exception.DataException;
import websocket.commands.MakeMoveCmd;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws DataException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        Notification notif = new Gson().fromJson(message, Notification.class);
                        notificationHandler.notify(notif);
                    }
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
                        Error err = new Gson().fromJson(message, Error.class);
                        notificationHandler.notify(err);
                    }
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
                        LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
                        notificationHandler.notify(loadGame);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterSession(String authToken, int gameID) throws DataException {

        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataException(500, ex.getMessage());
        }
    }

    public void exit(String authToken, int gameID) throws DataException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new DataException(500, ex.getMessage());
        }
    }

    public void resignGame(String authToken, int gameID) throws DataException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move, String teamColor) throws DataException {
        try {
            var action = new MakeMoveCmd(authToken, gameID, move, teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException e) {
            throw new DataException(500, e.getMessage());
        }
    }

}

