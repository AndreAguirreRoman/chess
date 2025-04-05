package client;

import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;
import model.AuthData;
import ui.EscapeSequences;

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


    public Repl (String serverUrl){
        preGameClient = new PreGameClient(serverUrl, this);
        inGameClient = new GameClient(serverUrl, this);
        authorizedClient = new AuthorizedClient(serverUrl, this);
    }

    public void run(){
        System.out.println("WELCOME TO CS240 CHESS");
        System.out.println("SIGN-IN TO START");
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
                    authToken = authorizedClient.getAuth();
                    System.out.println(result);


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            while (!result.equals("quit") && authToken != null && (observer || inGame)){
                printPrompt();
                String line = scanner.nextLine();
                try {
                    result = inGameClient.eval(line, username, authToken, teamColor, observer, inGame);
                    this.inGame = inGameClient.getInGame();
                    this.observer = inGameClient.getObserver();
                    System.out.println(result);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    //public void notify(Notification notification) {
    //    System.out.println(SET_BG_COLOR_BLUE + notification.message());
     //   printPrompt();
    //}

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }


    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        return null;
    }
}
