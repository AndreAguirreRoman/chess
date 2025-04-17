package client;
import chess.ChessBoard;
import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;


import exception.DataException;
import model.GameData;
import results.CreateGameRequest;
import results.GetGameRequest;
import results.UpdateGameRequest;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;

import static java.lang.Integer.parseInt;


public class AuthorizedClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    String username = null;
    String authToken = null;
    int gameID = 0;
    boolean inGame = false;
    boolean observer = false;
    String teamColor = null;
    private WebSocketFacade ws;
    public ChessGame chessGame;



    public AuthorizedClient(String serverUrl, NotificationHandler notificationHandler){
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input, String user, String token, String teamColor){
        try {
            username = user;
            authToken = token;
            this.teamColor = teamColor;
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help("");
                case "logout" -> logout(authToken, username);
                case "creategame" -> createGame(params);
                case "listgames" -> listGames(authToken);
                case "playgame" -> joinGame(params);
                case "watch" -> watchGame(params);
                case "quit" -> "quit";
                default -> help("INVALID COMMAND!");

            };
        } catch (DataException e){
            throw new RuntimeException(e);
        }

    }

    public String logout(String authToken, String username) throws DataException {
        server.logoutUser(authToken);
        this.username = null;
        this.authToken = null;
        return String.format(username + " you signed out successfully.");
    }

    public String createGame(String... params) throws DataException {
        if (params.length == 1) {
            CreateGameRequest createGameRequest = new CreateGameRequest(params[0], authToken);
            server.createGame(createGameRequest);
        } else {
            return "TRY AGAIN: Expected <Game Name>";
        }
        return String.format("Game created with name: %s.", (params[0]));
    }

    public String listGames(String authToken) throws DataException {
        Collection<GameData> games = server.getGames(authToken).games();
        StringBuilder gamesList = new StringBuilder();
        int index = 1;
        for (GameData game : games){
            gamesList.append(index++).append(" ").append(" || Game Name: ")
                    .append(game.gameName()).append(" || whitePlayer: ").append(game.whiteUsername() == null ? "Empty" : game.whiteUsername())
                    .append(" || blackPlayer: ").append(game.blackUsername() == null ? "Empty" : game.blackUsername()).append("\n");
        }
        return (gamesList.isEmpty()) ? "No games yet. Create ONE!"
            : String.format("GAMES: \n" + gamesList);


    }


    public String joinGame(String... params) throws DataException {
        if (params.length == 2) {
            try {
                if (!params[1].toUpperCase().equals("WHITE") && !params[1].toUpperCase().equals("BLACK")) {
                    return "You chose an invalid team! Choose from: white and black";
                }
                UpdateGameRequest updateGameRequest = new UpdateGameRequest(
                        parseInt(params[0]), params[1], authToken, null, "false", false);
                server.updateGame(updateGameRequest);
                this.gameID = parseInt(params[0]);
                this.inGame = true;
                this.teamColor = params[1];
                this.chessGame = getGame(parseInt(params[0]), authToken);
                ws = new WebSocketFacade(serverUrl, notificationHandler);
                ws.enterSession(this.authToken, this.gameID);
            } catch (NumberFormatException e) {
                return ("Error. Game ID must be an integer!");
            } catch (ArrayIndexOutOfBoundsException e) {
                return ("Missing parameters!");
            } catch (Exception e) {
                return ("Error joining: Invalid join");
            }
        } else {
            return ("Try again: EXPECTED <gameID> <teamColor>");
        }
        return String.format("Success! Joined as: " + (params[1]) +" color.");
    }

    public ChessGame getGame(int gameID, String authToken) throws DataException {
        Collection<GameData> games = server.getGames(authToken).games();
        GameData game = null;
        for (GameData g : games){
            if (g.gameID() == gameID){
                game = g;
                break;
            }
        }
        return game.game();
    }

    public String watchGame(String... params) throws DataException {
        this.inGame = true;
        this.observer = true;
        this.gameID = parseInt(params[0]);
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        ws.enterSession(this.authToken, this.gameID);
        return "Observing game!";
    }

    public ChessGame getChessBoard(){
        return chessGame;
    }

    public Boolean getInGame(){
        return inGame;
    }

    public void setInGame(boolean inGame){
        this.inGame = false;
        this.observer = false;
    }

    public Boolean getObserver(){
        return observer;
    }

    public String getTeamColor(){
        return teamColor;
    }

    public String getAuth(){
        return authToken;
    }

    public String getUsername(){
        return username;
    }

    public int getGameID(){
        return gameID;
    }


    public String help(String message){
        String commands = """
                - help -> Displays text informing the user what actions they can take.
                - quit -> Exits the program.
                - logout -> Logout.
                - creategame <game name> -> Register to game.
                - listgames -> List the games available.
                - playgame <gameID> <teamColor> -> Join a game to play.
                - watch <gameID> -> Observe a game.
                """;
        return (!message.isEmpty() ? (message) + ("\n") + commands : commands);
    }



}
