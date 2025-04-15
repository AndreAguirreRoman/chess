package client;

import client.websocket.NotificationHandler;
import com.google.gson.Gson;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;
import java.util.Scanner;


public class Repl implements NotificationHandler {
    private final GameClient inGameClient;
    private final PreGameClient preGameClient;
    private final AuthorizedClient authorizedClient;
    String authToken = null;
    String username = null;
    boolean inGame = false;
    boolean observer = false;
    String teamColor = null;
    int gameID = 0;


    public Repl (String serverUrl){
        preGameClient = new PreGameClient(serverUrl, this);
        inGameClient = new GameClient(serverUrl, this);
        authorizedClient = new AuthorizedClient(serverUrl, this);
    }

    public void run(){
        System.out.println("WELCOME TO CS240 CHESS");
        System.out.println("SIGN-IN TO START \n");

        System.out.println(preGameClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {
            while (!result.equals("quit") && authToken == null){
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = preGameClient.eval(line);
                    if (preGameClient.getAuthToken() != null) {
                        authToken = preGameClient.getAuthToken();
                        username = preGameClient.getUsername();
                    }
                    System.out.println(result);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            while (!result.equals("quit") && authToken != null && !inGame){
                printPrompt();
                String line = scanner.nextLine();
                try {
                    result = authorizedClient.eval(line, username, authToken, teamColor);

                    inGame = authorizedClient.getInGame();
                    observer = authorizedClient.getObserver();
                    teamColor = authorizedClient.getTeamColor();

                    String authToken = authorizedClient.getAuth();
                    this.authToken = authToken;

                    preGameClient.setAuthToken(authToken);

                    this.username = authorizedClient.getUsername();
                    this.gameID = authorizedClient.getGameID();
                    preGameClient.setUsername(this.username);

                    System.out.println(result);


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            while (!result.equals("quit") && authToken != null && (observer || inGame)){
                printPrompt();
                String line = scanner.nextLine();
                try {
                    result = inGameClient.eval(line, username, authToken, teamColor, observer, inGame, this.gameID);

                    boolean gameStatus = inGameClient.getInGame();
                    this.inGame = gameStatus;
                    this.observer = gameStatus;
                    this.authToken = authorizedClient.getAuth();
                    authorizedClient.setInGame(gameStatus);

                    System.out.println(result);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void notify(ServerMessage notification) {
        if (notification instanceof Notification notif) {
            System.out.println("[NOTIFICATION] " + notif.getMessage());
        }
        if (notification instanceof LoadGame load){
            System.out.println("[LOAD_GAME] " + load.getMessage());
        }
        if (notification instanceof Error err){
            System.out.println("[ERROR] " + err.getMessage());
        }
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

}
