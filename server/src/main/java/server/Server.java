package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import results.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Response;
import spark.Request;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
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
        Spark.delete("/db", this::clear);
        Spark.get("/game", this::listGames);
        Spark.post("/user", this::createUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);

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
            String authToken = req.headers("authorization");
            LogoutResponse result = userService.logout(new LogoutRequest(authToken));
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

    private Object listGames(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            GetGameResponse games = gameService.getGames(authToken);
            res.status(200);
            return new Gson().toJson(games);
        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return e.toJson();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message:", e.getMessage()));
        }
    }

    private Object createGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            if (authToken == null) {
                throw new DataAccessException(401, "unauthorized");
            }
            CreateGameRequest gameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
            if (gameName == null){
                throw new DataAccessException(400, "bad request");
            }
            CreateGameResponse game = gameService.createGame(gameName);
            res.status(200);
            return new Gson().toJson(game.gameId());
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message:", e.getMessage()));
        }
    }

    private Object updateGame(Request req, Response res){
        try {
            String authData = req.headers("authorization");
            UpdateGameRequest gameInfo = new Gson().fromJson(req.body(), UpdateGameRequest.class);
            UpdateGameRequest updateGame = new UpdateGameRequest(gameInfo.gameId(), authData, gameInfo.playerColor());
            UpdateGameResponse updateGameResponse = gameService.updateGame(updateGame);
            res.status(200);
            return new Gson().toJson(updateGameResponse.code());
        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return e.toJson();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message:", e.getMessage()));
        }
    }


    private Object clear(Request req, Response res) {
        try {
            clearService.deleteEverything();
            res.status(200);
            return new Gson().toJson(Map.of());
        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return e.toJson();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message:", e.getMessage()));
        }
    }


}
