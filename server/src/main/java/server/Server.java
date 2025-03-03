package server;

import dataaccess.DataAccess;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Response;
import spark.Request;
import com.google.gson.Gson;
import java.util.UUID;

public class Server {

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    public Server(DataAccess dataAccess){
        this.clearService = new ClearService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.clearService = new ClearService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        // Spark.get("/game", this::getGames);
        Spark.post("/user/register", this::createUser);
        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object createUser(Request req, Response res){
        return res;
    }



    /*private Object getGames(Request req, Response res) {

    }*/


}
