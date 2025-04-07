package client.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;

import exception.DataException;
import websocket.commands.UserGameCommand;
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
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
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

    public void enterSession(String authToken, String username, int gameID) throws DataException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, username, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new DataException(500, ex.getMessage());
        }
    }

    public void exit(String authToken, String username, int gameID) throws DataException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, username, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new DataException(500, ex.getMessage());
        }
    }

    public void resignGame(String authToken, String username, int gameID) throws DataException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, username, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new DataException(500, ex.getMessage());
        }
    }

}

