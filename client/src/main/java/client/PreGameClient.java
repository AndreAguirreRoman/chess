package client;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;

import dataaccess.DataAccessException;
import results.LoginRequest;
import results.LoginResult;
import results.RegisterRequest;
import results.RegisterResult;
import client.server.ServerFacade;

import java.util.Arrays;

public class PreGameClient {
    private final ServerFacade server;
    private WebSocketFacade ws;
    String username = null;
    String authToken = null;

    public PreGameClient(String serverUrl, NotificationHandler notificationHandler){
        server = new ServerFacade(serverUrl);

    }

    public String eval(String input){
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd){
                case "help" -> help();
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String login(String... params) throws DataAccessException {
        if (params.length == 2) {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            LoginResult loginResult = server.loginUser(loginRequest);
            username = loginResult.username();
            authToken = loginResult.authToken();

        }
        return (username != null) ? String.format("You signed in as %s.", username) :
                String.format("TRY AGAIN: Expected <username> <password>");
    }

    public String register(String... params) throws DataAccessException {
        if (params.length == 3) {
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult = server.addUser(registerRequest);
            username = registerResult.username();
            authToken = registerResult.authToken();
            System.out.println(registerResult);
        }
        return (username != null) ? String.format("You registered as %s.", username) :
                String.format("TRY AGAIN: Expected <username> <password> <email>");

    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername(){
        return username;
    }

    public String help(){
        return """
                - help -> Displays text informing the user what actions they can take.
                - quit -> Exits the program.
                - login <username> -> Login to the server.
                - register -> Register to game.
                """;
    }

}
