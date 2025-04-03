package client;
import com.sun.nio.sctp.NotificationHandler;
import dataaccess.DataAccessException;
import model.GameData;
import results.*;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public class AuthorizedClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    String username = null;
    String authToken = null;
    boolean inGame = false;

    public AuthorizedClient(String serverUrl, NotificationHandler notificationHandler){
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input, String user, String token){
        try {
            username = user;
            authToken = token;
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "logout" -> logout(authToken, username);
                case "create game" -> createGame(params);
                case "list games" -> listGames(authToken);
                case "play game" -> joinGame(params);
                case "observe game" -> watchGame(params);
                case "quit" -> "quit";
                default -> help();

            };
        } catch (DataAccessException e){
            throw new RuntimeException(e);
        }

    }

    public String logout(String authToken, String username) throws DataAccessException {
        server.logoutUser(authToken);
        return String.format(username + " ,you signed out succesfully %s.");
    }

    public String createGame(String... params) throws DataAccessException {
        int gameID = 0;
        if (params.length == 1) {
            CreateGameRequest createGameRequest = new CreateGameRequest(params[0], authToken);
            CreateGameResponse createGameResponse = server.createGame(createGameRequest);
            gameID = createGameResponse.gameId();
            System.out.println("Succesfully created game!");
        }
        return String.format("Game created with ID: %d.", gameID);
    }

    public String listGames(String authToken) throws DataAccessException {
        Collection<GameData> games = server.getGames(authToken).games();
        StringBuilder gamesList = new StringBuilder();

        for (GameData game : games){
            gamesList.append("ID: ").append(game.gameID()).append("Game Name: ")
                    .append(game.gameName()).append("White player: ").append(game.whiteUsername())
                    .append("Black player: ").append(game.blackUsername()).append("\n");
        }
        return (gamesList.isEmpty()) ? "No games yet. Create ONE!"
            : String.format("GAMES: \n" + gamesList);


    }

    public Boolean getInGame(){
        return inGame;
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
