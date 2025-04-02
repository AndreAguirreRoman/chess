package client;
import com.sun.nio.sctp.NotificationHandler;
import dataaccess.DataAccessException;
import results.LoginRequest;
import results.LoginResult;
import results.RegisterRequest;
import results.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;

public class PreGameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    String username = null;
    String authToken = null;

    public PreGameClient(String serverUrl, NotificationHandler notificationHandler){
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
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
                default -> throw new IllegalStateException("Unexpected value: " + cmd);
            };
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String login(String... params) throws DataAccessException {
        if (params.length >= 1) {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            LoginResult loginResult = server.loginUser(loginRequest);
            username = loginResult.username();
            authToken = loginResult.authToken();
            return String.format("You signed in as %s.", username);
        }
        throw new DataAccessException(400, "Expected: <yourname>");
    }

    public String register(String... params) throws DataAccessException {
        if (params.length > 2){
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult = server.addUser(registerRequest);
            username = registerResult.userName();
            authToken = registerResult.authToken();
            return String.format("You registered as %s.", username);
        }
        throw new DataAccessException(400, "Expected: <username> <password> <email>");
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
