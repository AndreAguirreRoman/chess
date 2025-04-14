package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.UserData;
import results.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Response;
import spark.Request;
import com.google.gson.Gson;
import spark.Spark;
import websocket.WebSocketHandler;

import java.util.Map;

public class Server {

    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;
    private final WebSocketHandler webSocketHandler;


    public Server(){
        MySqlDataAccess sql;
        try {
            sql = new MySqlDataAccess();
            webSocketHandler = new WebSocketHandler();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        DataAccess dataAccess = new MemoryDataAccess();
        this.clearService = new ClearService(sql);
        this.gameService = new GameService(sql);
        this.userService = new UserService(sql);


    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);
        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.get("/game", this::getGames);
        Spark.put("/game", this::updateGame);
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
            return new Gson().toJson(Map.of(
                    "username", authData.userName(),
                    "authToken", authData.authToken()
            ));
        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return e.toJson();
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message: Error line 79", e.getMessage()));
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
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message:", e.getMessage()));
        }
    }

    private Object getGames(Request req, Response res){
        try {
            String authToken = req.headers("authorization");
            GetGameResponse games = gameService.getGames(authToken);
            res.status(200);
            var response = new Gson().toJson(games);
            return response;
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

            CreateGameRequest gameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
            CreateGameRequest gameData = new CreateGameRequest(gameName.gameName(), authToken);

            CreateGameResponse game = gameService.createGame(gameData);
            res.status(200);
            return new Gson().toJson(Map.of("gameID", game.gameId()));

        } catch (DataAccessException e) {
            res.status(e.getStatusCode());
            return new Gson().toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object updateGame(Request req, Response res){
        try {
            String authToken = req.headers("authorization");

            String requestBody = req.body();

            UpdateGameRequest gameInfo = new Gson().fromJson(requestBody, UpdateGameRequest.class);
            UpdateGameRequest gameInfoWithAuth = new UpdateGameRequest(
                    gameInfo.gameID(), gameInfo.playerColor(), authToken
            );


            UpdateGameResponse updateGameResponse = gameService.updateGame(gameInfoWithAuth);

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