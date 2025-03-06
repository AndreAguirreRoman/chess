package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import results.LoginResult;
import results.LogoutRequest;
import results.LogoutResponse;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Response;
import spark.Request;
import com.google.gson.Gson;

import java.util.Map;

public class Server {

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;


    public Server(){
        DataAccess dataAccess = new MemoryDataAccess();
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
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);

        // Spark.delete("/db", this::clear);

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
            res.status(e.getStatusCode());
            return e.toJson();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message:", e.getMessage()));
        }
    }

    private Object login(Request req, Response res){
        try {
            var login = new Gson().fromJson(req.body(), UserData.class);
            LoginResult userLogin = userService.login(login);
            res.status(200);
            return new Gson().toJson(userLogin);
        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return e.toJson();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message:", e.getMessage()));
        }
    }

    private Object logout(Request req, Response res){
        try {
            var logout = new Gson().fromJson(req.headers("authorization"), LogoutRequest.class);
            LogoutResponse result = userService.logout(logout);
            res.status(200);
            return new Gson().toJson(result);
        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return e.toJson();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message:", e.getMessage()));
        }
    }

/*
    private Object clear(Request req, Response res) {
        try {
            clearService.deleteEverything();
            res.status(200);
            return new Gson().toJson("Deleted!");
        } catch (DataAccessException e) {
            ResponseException ex = ResponseException.error(e.getMessage());
            exceptionHandler(ex, req, res);
            return res.body();
        }
    }

    */



    /*private Object getGames(Request req, Response res) {

    }*/

}
