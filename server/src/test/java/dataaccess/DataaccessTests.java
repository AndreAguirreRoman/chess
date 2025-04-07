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
        private DataAccess dataAccess;

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

            user = new UserData(1, "SOFIA","BYU@byu.com", "password");
            userTwo = new UserData(2, "KAREL","BYU@byu.com", "password");
            createGameResponse = new CreateGameResponse(1);

        }

    //"createUser, getUser, clear, createAuth, getAuth, deleteAuth, createGame, getGame, getGames, updateGame"

        @Test
        @Order(1)
        @DisplayName("Create User DATAACCESS")
        public void createUserData(){
            AuthData registerResult = assertDoesNotThrow(() -> userService.register(user),
                    "SOMETHING WENT WRONG");
            assertEquals(user.username(), registerResult.userName(),
                    "DIFFERENT USERNAMES");
        }

        @Test
        @Order(2)
        @DisplayName("Create User Error")
        public void createUserErrorData(){
            UserData userWrong = new UserData(2, null,"BYU@byu.com", "password");
            DataAccessException e = assertThrows(DataAccessException.class, ()-> userService.register(userWrong));
            assertEquals("Error bad request",e.getMessage());
        }

        @Test
        @Order(3)
        @DisplayName("Login Good one")
        public void loginData() throws DataAccessException{
            userService.register(userTwo);
            LoginResult loginResult = assertDoesNotThrow(() -> userService.login(userTwo));

            assertEquals(userTwo.username(), loginResult.username());
        }

        @Test
        @Order(4)
        @DisplayName("Get Auth")
        public void getAuthData() throws DataAccessException{
            userService.register(user);
            LoginResult loginResult = assertDoesNotThrow(() -> userService.login(user));
            String authToken = loginResult.authToken();
            assertNotNull(authToken);
        }

        @Test
        @Order(5)
        @DisplayName("Get Auth")
        public void getAuthErrorData() throws DataAccessException{
            UserData wrongUser = new UserData(100, "uwu", "aa@jaja.com", null);
            DataAccessException e = assertThrows(DataAccessException.class, () -> userService.register(wrongUser), "SOMETHING WENT WRONG");
            assertEquals("Error bad request",e.getMessage());

        }

        @Test
        @Order(6)
        @DisplayName("Get User")
        public void getUser() throws DataAccessException{
            userService.register(user);
            UserData getResult = assertDoesNotThrow(() -> userService.getUser(user.username()));
            assertEquals(user.username(), getResult.username());
        }

        @Test
        @Order(7)
        @DisplayName("Get User Error")
        public void getUserError() throws DataAccessException{
            userService.register(user);
            DataAccessException e = assertThrows(DataAccessException.class, () -> userService.getUser(user.email()), "SOMETHING WENT WRONG");
            assertEquals("Error bad request",e.getMessage());

        }

        @Test
        @Order(8)
        @DisplayName("Login Error")
        public void loginError(){
            DataAccessException e = assertThrows(DataAccessException.class, () -> userService.login(user), "SOMETHING WENT WRONG");
            assertEquals("Error: unauthorized",e.getMessage());
        }




        @Test
        @Order(9)
        @DisplayName("Logout")
        public void logout() throws DataAccessException{
            AuthData user = userService.register(userTwo);
            LogoutResponse logoutResponse = userService.logout(new LogoutRequest(user.authToken()));
            assertEquals(200, logoutResponse.code());
        }

        @Test
        @Order(10)
        @DisplayName("Logout Error")
        public void logoutError(){
            LogoutRequest logoutRequest = new LogoutRequest(null);

            DataAccessException e = assertThrows(DataAccessException.class, () -> userService.logout(logoutRequest), "SOMETHING WENT WRONG");
            assertEquals("Error: unauthorized",e.getMessage());
        }

        @Test
        @Order(11)
        @DisplayName("Create Game")
        public void createGame() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            System.out.println(userAuth);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());

            CreateGameResponse gameResponse = gameService.createGame(gameRequest);
            assertEquals(createGameResponse.gameId(), gameResponse.gameId());
        }



        @Test
        @Order(12)
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
        @Order(13)
        @DisplayName("Get Games SQL")
        public void getGamesSql() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequestOne = new CreateGameRequest("game", userAuth.authToken());
            CreateGameRequest gameRequestTwo = new CreateGameRequest("game2", userAuth.authToken());
            CreateGameRequest gameRequestThree = new CreateGameRequest("game3", userAuth.authToken());
            CreateGameRequest gameRequestFour = new CreateGameRequest("game4", userAuth.authToken());
            CreateGameRequest gameRequestFive = new CreateGameRequest("game5", userAuth.authToken());


            gameService.createGame(gameRequestOne);
            gameService.createGame(gameRequestTwo);
            gameService.createGame(gameRequestThree);
            gameService.createGame(gameRequestFour);
            gameService.createGame(gameRequestFive);

            Collection<GameData> expectedGames = new ArrayList<>();
            expectedGames.add(new GameData(1, null, null, "game", null));
            expectedGames.add(new GameData(2, null, null, "game2", null));
            expectedGames.add(new GameData(3, null, null, "game3", null));
            expectedGames.add(new GameData(4, null, null, "game4", null));
            expectedGames.add(new GameData(5, null, null, "game5", null));

            GetGameResponse getGameResponse = gameService.getGames(userAuth.authToken());
            assertEquals(expectedGames.size(), getGameResponse.games().size());

        }



        @Test
        @Order(14)
        @DisplayName("Get Games Error in SQL")
        public void getGamesErrorSql() throws DataAccessException{
            AuthData userAuth = userService.register(userTwo);
            LogoutRequest lR = new LogoutRequest(userAuth.authToken());
            userService.logout(lR);

            DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.getGames(userAuth.authToken()), "SOMETHING WENT WRONG");
            assertEquals("Get auth = Error unauthorized",e.getMessage());
        }

        @Test
        @Order(15)
        @DisplayName("Update Game")
        public void updateGame() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());

            gameService.createGame(gameRequest);
            GameData updatedGameExpected = new GameData(1, "SOFIA", null, "test game", null);

            UpdateGameRequest updateGameRequest = new UpdateGameRequest(1, "white", userAuth.authToken());
            gameService.updateGame(updateGameRequest);

            GameData updatedGameResponse = gameService.getGame(1);
            assertEquals(updatedGameExpected, updatedGameResponse);
        }



        @Test
        @Order(16)
        @DisplayName("Update Game Error in SQL")
        public void updateGameErrorSql() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game Error", userAuth.authToken());

            gameService.createGame(gameRequest);
            gameService.createGame(gameRequest);
            UpdateGameRequest updateGameRequest = new UpdateGameRequest(2, "WHISHIS", userAuth.authToken());

            DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.updateGame(updateGameRequest), "SOMETHING WENT WRONG");
            assertEquals("Error bad request",e.getMessage());
        }

        @Test
        @Order(17)
        @DisplayName("Get game")
        public void getGame() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());
            CreateGameResponse gameID = gameService.createGame(gameRequest);

            GameData game = gameService.getGame(1);
            GameData expectedGame = new GameData(1, null, null, "test game", null);
            assertEquals(expectedGame, game);
        }

        @Test
        @Order(18)
        @DisplayName("GET Game Error")
        public void getGameError() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());

            CreateGameResponse gameID  = gameService.createGame(gameRequest);
            DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.getGame(gameID.gameId() + 1), "SOMETHING WENT WRONG");
            assertEquals("Error bad request",e.getMessage());
        }


        @Test
        @Order(19)
        @DisplayName("Clear all")
        public void clear(){
            assertDoesNotThrow(() -> clearService.deleteEverything());
        }

        @Test
        @Order(20)
        @DisplayName("Update Game TWO")
        public void updateGameTwo() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());

            gameService.createGame(gameRequest);
            GameData updatedGameExpected = new GameData(1, null, "SOFIA", "test game", null);

            UpdateGameRequest updateGameRequest = new UpdateGameRequest(1, "black", userAuth.authToken());
            gameService.updateGame(updateGameRequest);

            GameData updatedGameResponse = gameService.getGame(1);
            assertEquals(updatedGameExpected, updatedGameResponse);
        }



        @Test
        @Order(21)
        @DisplayName("Update Game Error TWO")
        public void updateGameErrorTwo() throws DataAccessException{
            AuthData userAuth = userService.register(user);
            CreateGameRequest gameRequest = new CreateGameRequest("test game", userAuth.authToken());

            gameService.createGame(gameRequest);
            UpdateGameRequest updateGameRequest = new UpdateGameRequest(null, "white", userAuth.authToken());

            DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.updateGame(updateGameRequest), "SOMETHING WENT WRONG");
            assertEquals("Error bad request",e.getMessage());
        }



    }
