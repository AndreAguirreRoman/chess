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
    boolean observer = false;
    int gameID = Integer.parseInt(null);


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
                case "createGame" -> createGame(params);
                case "listGames" -> listGames(authToken);
                case "playGame" -> joinGame(params);
                case "watchGame" -> watchGame(params);
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
        } else {
            return "TRY AGAIN: Expected <Game Name>"
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

    public String joinGame(String... params) throws DataAccessException {
        if (params.length == 2) {
            try {
                UpdateGameRequest updateGameRequest = new UpdateGameRequest(
                        Integer.parseInt(params[0]), params[1], authToken);
                server.updateGame(updateGameRequest);
                gameID = Integer.parseInt(params[0]);
                inGame = true;
            } catch (Exception e){
                return ("Error joinning: " + e.getMessage());
            }
        } else {
            return ("Try again: EXPECTED <gameID> <teamColor>");
        }

        return String.format("Success! Joined as: " + (params[1]) +" color.");
    }

    public String watchGame(String... params){
        return "HI";
    }

    public Boolean getInGame(){
        return inGame;
    }

    public Boolean getObserver(){
        return observer;
    }

    public int getGameID(){
        return gameID;
    }


    public String help(){
        return """
                - help -> Displays text informing the user what actions they can take.
                - quit -> Exits the program.
                - logout -> Logout.
                - createGame <game name>-> Register to game.
                - listGames -> List the games available.
                - playGame <gameID> <teamColor> -> Join a game to play.
                - watchGame <gameID> -> Observe a game.
                """;
    }

}
