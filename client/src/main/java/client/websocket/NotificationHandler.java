package client.websocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(websocket.messages.ServerMessage notification);
}
