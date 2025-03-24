package dataaccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import results.*;
import server.Server;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;
import java.util.Collection;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class DataaccessTests {

        private ClearService clearService;
        private UserService userService;
        private GameService gameService;
        private static Server server;
        private static UserData user;
        private static UserData userTwo;
        private static CreateGameResponse createGameResponse;

        @BeforeEach
        public void setUp() throws DataAccessException {

            DataAccess dataAccess = new MySqlDataAccess();
            this.clearService = new ClearService(dataAccess);
            this.gameService = new GameService(dataAccess);
            this.userService = new UserService(dataAccess);
            dataAccess.clear();
        }

        @AfterAll
        static void stopServer() {
            server.stop();
        }

        @BeforeAll
        public static void init() {
            server = new Server();
            var port = server.run(9080);

            user = new UserData(1, "ANDRE","A@A.COM", "BYU");
            userTwo = new UserData(2, "KAREL","A@A.COM", "BYU");
            createGameResponse = new CreateGameResponse(1);

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
        @Order(7)
        @DisplayName("Create Game")
        public void createGame() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            System.out.println(userAuth);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());

            CreateGameResponse gameResponse = gameService.createGame(gameRequest);
            assertEquals(createGameResponse.gameId(), gameResponse.gameId());
        }



        @Test
        @Order(8)
        @DisplayName("Create Game Error")
        public void createGameError() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            LogoutRequest lR = new LogoutRequest(userAuth.authToken());
            userService.logout(lR);

            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());
            DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.createGame(gameRequest), "SOMETHING WENT WRONG");
            assertEquals("Get auth = Error unauthorized",e.getMessage());
        }

        @Test
        @Order(9)
        @DisplayName("Get Games")
        public void getGames() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());
            CreateGameRequest gameRequest2 = new CreateGameRequest("test game2", userAuth.authToken());
            CreateGameRequest gameRequest3 = new CreateGameRequest("test game3", userAuth.authToken());

            gameService.createGame(gameRequest);
            gameService.createGame(gameRequest2);
            gameService.createGame(gameRequest3);

            Collection<GameData> expectedGames = new ArrayList<>();
            expectedGames.add(new GameData(1, null, null, "test game", null));
            expectedGames.add(new GameData(2, null, null, "test game2", null));
            expectedGames.add(new GameData(3, null, null, "test game3", null));

            GetGameResponse getGameResponse = gameService.getGames(userAuth.authToken());
            assertEquals(expectedGames.size(), getGameResponse.games().size());

        }



        @Test
        @Order(10)
        @DisplayName("Get Games Error")
        public void getGamesError() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            LogoutRequest lR = new LogoutRequest(userAuth.authToken());
            userService.logout(lR);

            DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.getGames(userAuth.authToken()), "SOMETHING WENT WRONG");
            assertEquals("Get auth = Error unauthorized",e.getMessage());
        }

        @Test
        @Order(11)
        @DisplayName("Update Game")
        public void updateGame() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());

            gameService.createGame(gameRequest);
            GameData updatedGameExpected = new GameData(1, "ANDRE", null, "test game", null);

            UpdateGameRequest updateGameRequest = new UpdateGameRequest(1, "white", userAuth.authToken());
            gameService.updateGame(updateGameRequest);

            GameData updatedGameResponse = gameService.getGame(1);
            assertEquals(updatedGameExpected, updatedGameResponse);
        }



        @Test
        @Order(12)
        @DisplayName("Update Game Error")
        public void updateGameError() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());

            gameService.createGame(gameRequest);
            UpdateGameRequest updateGameRequest = new UpdateGameRequest(1, null, userAuth.authToken());

            DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.updateGame(updateGameRequest), "SOMETHING WENT WRONG");
            assertEquals("Error bad request",e.getMessage());
        }





        @Test
        @Order(13)
        @DisplayName("Clear all")
        public void clear(){
            assertDoesNotThrow(() -> clearService.deleteEverything());
        }



    }
