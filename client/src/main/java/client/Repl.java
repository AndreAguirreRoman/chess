package client;

import com.sun.nio.sctp.HandlerResult;
import com.sun.nio.sctp.Notification;
import com.sun.nio.sctp.NotificationHandler;
import ui.EscapeSequences;

import static ui.EscapeSequences.*;
import java.util.Scanner;


public class Repl implements NotificationHandler {
    //private final InGameClient inGameClient;
    private final PreGameClient preGameClient;
    //private final AuthorizedClient authorizedClient;

    public Repl (String serverUrl){
        preGameClient = new PreGameClient(serverUrl, this);
    }

    public void run(){
        System.out.println("WELCOME TO CS240 CHESS");
        System.out.println("SIGN-IN TO START");
        System.out.println(preGameClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = preGameClient.eval(line);
                System.out.println(SET_BG_COLOR_DARK_GREEN + result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
    }

    //public void notify(Notification notification) {
    //    System.out.println(SET_BG_COLOR_BLUE + notification.message());
     //   printPrompt();
    //}

    private void printPrompt() {
        System.out.print("\n" + SET_BG_COLOR_BLUE + ">>> " + SET_BG_COLOR_BLUE);
    }


    @Override
    public HandlerResult handleNotification(Notification notification, Object attachment) {
        return null;
    }
}
