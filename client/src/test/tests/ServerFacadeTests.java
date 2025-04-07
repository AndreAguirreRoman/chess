package tests;

import dataaccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;
import results.CreateGameRequest;
import results.LoginRequest;
import results.RegisterRequest;
import server.Server;
import client.server.ServerFacade;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private final RegisterRequest registerRequestTest = new RegisterRequest("player1",
            "password", "p1@email.com");

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        facade.deleteDatabase();
    }

    @BeforeEach
    void resetDB() throws DataAccessException {
        facade.deleteDatabase();
    }

    @AfterAll
    static void stopServer() throws DataAccessException {
        facade.deleteDatabase();
        server.stop();
    }

    @Test
    void addUser() throws Exception {
        var authData = facade.addUser(registerRequestTest);
        assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void addUserError() throws Exception {

        assertThrows(DataAccessException.class, () ->
                facade.addUser(null));
    }

    @Test
    void loginUser() throws Exception {
        facade.addUser(registerRequestTest);
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        var authData = facade.loginUser(loginRequest);
        assertTrue(authData.username() != null);
    }

    @Test
    void loginUserError() throws Exception {
        assertThrows(DataAccessException.class, () ->
                facade.loginUser(null));
    }

    @Test
    void getGames() throws Exception {
        var authData = facade.addUser(registerRequestTest);
        CreateGameRequest createGameRequest = new CreateGameRequest("Test1", authData.authToken());
        facade.createGame(createGameRequest);
        Collection<GameData> games = facade.getGames(authData.authToken()).games();
        assertFalse(games.isEmpty());
    }

    @Test
    void getGamesError() throws Exception {
        assertThrows(DataAccessException.class, () ->
                facade.getGames(null));
    }


    @Test
    void logoutUser() throws Exception {
        var authData = facade.addUser(registerRequestTest);
        try {
            facade.logoutUser(authData.authToken());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void logoutUserError() throws Exception {
        assertThrows(DataAccessException.class, () ->
                facade.logoutUser(null));
    }
    @Test
    void createGame() throws Exception {
        var authData = facade.addUser(registerRequestTest);
        CreateGameRequest createGameRequest = new CreateGameRequest("Test1", authData.authToken());
        CreateGameRequest createGameRequestTwo = new CreateGameRequest("Test2", authData.authToken());

        facade.createGame(createGameRequest);
        facade.createGame(createGameRequestTwo);

        Collection<GameData> games = facade.getGames(authData.authToken()).games();
        assertTrue(games.size() > 1);
    }

    @Test
    void createGameError() throws Exception {
        assertThrows(NullPointerException.class, () ->
                facade.createGame(null));
    }
    @Test
    void deleteDb() throws Exception {
        try {
            facade.deleteDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
