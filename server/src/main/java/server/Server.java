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
        Spark.put("/game", this::updateGame);

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
            return new Gson().toJson(Map.of(
                    "username", authData.userName(),
                    "authToken", authData.authToken()
            ));
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
            System.out.println("Logout error: " + e.getMessage());
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
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
            AuthData authData = userService.getUserAuth(authToken);

            CreateGameRequest gameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
            CreateGameRequest gameData = new CreateGameRequest(gameName.gameName(), authToken);

            CreateGameResponse game = gameService.createGame(gameData);

            res.status(200);
            System.out.println(res.status());
            System.out.println("Created game with ID: " + game.gameId());

            return new Gson().toJson(Map.of("gameID:", game.gameId()));

        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object updateGame(Request req, Response res){
        try {
            String authData = req.headers("authorization");
            System.out.println(authData);
            UpdateGameRequest gameInfo = new Gson().fromJson(req.body(), UpdateGameRequest.class);
            System.out.println("Game info" + gameInfo);
            UpdateGameRequest gameInfoAuthToken = new UpdateGameRequest(gameInfo.gameId(),authData, gameInfo.playerColor());
            System.out.println(gameInfoAuthToken);


            UpdateGameResponse updateGameResponse = gameService.updateGame(gameInfoAuthToken);
            System.out.println(updateGameResponse);
            res.status(200);
            return new Gson().toJson(updateGameResponse);
        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return e.toJson();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("Error", e.getMessage()));
        }
    }

    /*private Object updateGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");

            UpdateGameRequest gameInfo = new Gson().fromJson(req.body(), UpdateGameRequest.class);
            if (gameInfo == null || gameInfo.gameId() <= 0 || gameInfo.playerColor() == null) {
                throw new DataAccessException(400, "Bad request - Missing required fields");
            }

            UpdateGameResponse updateGameResponse = gameService.updateGame(gameInfo);
            res.status(200);
            return new Gson().toJson(updateGameResponse);
        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

     */



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
