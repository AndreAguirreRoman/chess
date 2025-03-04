package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Response;
import spark.Request;
import com.google.gson.Gson;
import exception.ResponseException;
public class Server {

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;


    public Server(DataAccess dataAccess){
        this.clearService = new ClearService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.userService = new UserService(dataAccess);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        // Spark.get("/game", this::getGames);
        Spark.post("/user/register", this::createUser);
        Spark.delete("/db", this::clear);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object createUser(Request req, Response res) {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = userService.register(user);
            res.status(200);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            res.status(e.StatusCode());
            return e.toJson();
        }
    }

    private Object clear(Request req, Response res) {
        try {
            clearService.deleteEverything();
            res.status(200);
            return new Gson().toJson("Deleted!");
        } catch (DataAccessException e) {
            res.status(e.StatusCode());
            return e.toJson();
        }
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }


    /*private Object getGames(Request req, Response res) {

    }*/


}
