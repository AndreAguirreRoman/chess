package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Connection>> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session, int gameID) {
        var connection = new Connection(visitorName, session);
        ArrayList<Connection> connectionList = connections.get(gameID);

        if (!connections.containsKey(gameID)){
            ArrayList<Connection> newList = new ArrayList<>();
            newList.add(connection);
            connections.put(gameID, newList);
        } else {

            connectionList.add(connection);
            connections.put(gameID, connectionList);
        }

    }

    public void remove(String visitorName, int gameID) {
        ArrayList<Connection> connectionList = connections.get(gameID);
        if (connectionList == null) {
            return;
        }
        connectionList.removeIf(connection -> connection.username.equals(visitorName));

    }

    public void sendOneUser(String username, ServerMessage notification, int gameID) throws IOException {
        var connectionList = connections.get(gameID);
        if (connectionList == null) {
            System.out.println("No connections found for gameID: " + gameID);
            return;
        }
        for (var c : connectionList) {
            if (c.session.isOpen()) {
                if (c.username.equals(username)) {
                    c.send(new Gson().toJson(notification));
                    break;
                }
            }
        }


    }




    public void broadcast(String excludeVisitorName, ServerMessage notification, int gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        var connectionList = connections.get(gameID);


        if (connectionList == null) {
            System.out.println("No connections found for gameID: " + gameID);
            return;
        }
        for (var c : connectionList) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeVisitorName)) {
                    c.send(new Gson().toJson(notification));
                }
            } else {
                removeList.add(c);
            }
        }
        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c);
        }
    }
}
