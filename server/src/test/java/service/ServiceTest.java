package service;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import results.LoginResult;
import results.LogoutRequest;
import results.LogoutResponse;
import server.Server;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTest {

    private ClearService clearService;
    private UserService userService;
    private GameService gameService;
    private static Server server;
    private static UserData user;
    private static UserData userTwo;
    private String authToken;

    @BeforeEach
    public void setUp(){

        DataAccess dataAccess = new MemoryDataAccess();
        this.clearService = new ClearService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.userService = new UserService(dataAccess);


    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);

        user = new UserData(1, "ANDRE","A@A.COM", "BYU");
        userTwo = new UserData(2, "KAREL","A@A.COM", "BYU");

    }

    @Test
    @Order(1)
    @DisplayName("Register User")
    public void register(){
        AuthData registerResult = assertDoesNotThrow(() -> userService.register(user),
                "SOMETHING WENT WRONG");
        assertEquals(user.username(), registerResult.userName(),
                "DIFFERENT USERNAMES");
    }

    @Test
    @Order(2)
    @DisplayName("Register User Error")
    public void registerError(){
        UserData userWrong = new UserData(2, null,"A@A.COM", "BYU");
        DataAccessException e = assertThrows(DataAccessException.class, ()-> userService.register(userWrong));
        assertEquals("Error bad request",e.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Login")
    public void login() throws DataAccessException{
        userService.register(user);
        LoginResult loginResult = assertDoesNotThrow(() -> userService.login(user));
        assertEquals(user.username(), loginResult.username());
    }

    @Test
    @Order(4)
    @DisplayName("Login Error")
    public void loginError(){
        DataAccessException e = assertThrows(DataAccessException.class, () -> userService.login(user), "SOMETHING WENT WRONG");
        assertEquals("Error: unauthorized",e.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Logout")
    public void logout() throws DataAccessException{
        AuthData userAuth = userService.register(user);
        LogoutRequest logoutRequest = new LogoutRequest(userAuth.authToken());
        LogoutResponse logoutResponse = userService.logout(logoutRequest);
        assertEquals(200, logoutResponse.code());
    }

    @Test
    @Order(6)
    @DisplayName("Logout Error")
    public void logoutError(){

        LogoutRequest logoutRequest = new LogoutRequest("1234");
        DataAccessException e = assertThrows(DataAccessException.class, () -> userService.logout(logoutRequest), "SOMETHING WENT WRONG");
        assertEquals("Error: unauthorized",e.getMessage());
    }



    @Test
    @Order(10)
    @DisplayName("Clear all")
    public void clear(){
        assertDoesNotThrow(() -> clearService.deleteEverything());
    }



}
