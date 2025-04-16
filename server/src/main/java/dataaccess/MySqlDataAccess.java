package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }


    public UserData createUser(UserData user) throws DataAccessException{
        UserData userHashed = null;
        try {
            var statement = "INSERT INTO users (username, email, password, json) VALUES (?,?,?,?)";
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            userHashed = new UserData(user.id(), user.username(), user.email(), hashedPassword);
            var json = new Gson().toJson(userHashed);
            executeUpdate(statement, user.username(), user.email(), hashedPassword, json);
            UserData userForId = getUser(user.username());
            UserData userHashedFinal = new UserData(userForId.id(), user.username(), user.email(), hashedPassword);
            var nwJson = new Gson().toJson(userHashedFinal);
            String updateJson = "UPDATE users SET json = ? WHERE id = ?";
            executeUpdate(updateJson, nwJson, userForId.id());
            return userHashedFinal;
        } catch (Exception e){
            throw new DataAccessException(400, e.getMessage());
        }
    }


    public UserData getUser(String user) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public void clear() throws DataAccessException {
        try {
            List<String > tables = Arrays.asList("TRUNCATE TABLE users;","TRUNCATE TABLE games;",
                    "TRUNCATE TABLE auths;");
            for (String table : tables){
                executeUpdate(table);
            }
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }

    }

    public AuthData createAuth(UserData userData, String authToken)
            throws DataAccessException {
        try {
            var statement = "INSERT INTO auths (username, authtoken) VALUES (?,?)";
            executeUpdate(statement, userData.username(), authToken);
            return new AuthData(authToken, userData.username());
        } catch (Exception e) {
            throw new DataAccessException(400, e.getMessage());
        }
    }

    public AuthData getAuth(String authToken){
        AuthData authData = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        authData = new AuthData(authToken, rs.getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return authData;
    }

    public AuthData getUserByAuth(String authToken){
        AuthData authData = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        authData = new AuthData(authToken, rs.getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return authData;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auths where authtoken =?";
        executeUpdate(statement, authToken);
    }

    public GameData createGame(GameData gameData) throws DataAccessException {
        try {
            var statement = "INSERT INTO games (whiteplayer, blackplayer, gamename, game) VALUES (? ,?, ?, ?)";
            var gamejson = new Gson().toJson(gameData.game());
            int gameID = executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gamejson);
            return new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        } catch (Exception e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }

    public GameData getGame(int id) {
        GameData game = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        game = readGame(rs);
                        System.out.println(game.gameName());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return game;
    }

    public Collection<GameData> getGames() {
        Collection<GameData> result = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public void updateGame(int gameId, String authToken, String playerColor) throws DataAccessException {
        String playerColorUpdate = playerColorDecider(playerColor);

        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("Username to join: " + authToken);
            var statement = "UPDATE games SET " + playerColorUpdate + " = ? WHERE id = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(2, gameId);
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (Exception e){
            throw new DataAccessException(500, e.getMessage());
        }
    }

    public void updateBoard(int gameID, String board) throws DataAccessException{

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET game = ? WHERE id = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(2, gameID);
                ps.setString(1, board);
                ps.executeUpdate();
            }
        } catch (Exception e){
            throw new DataAccessException(500, e.getMessage());
        }
    }

    private String playerColorDecider(String playerColor){
        System.out.println("Player color in decider " + playerColor);
        if (playerColor.toUpperCase().equals("WHITE")) {
            return "whiteplayer";
        }
        else {
            return "blackplayer";
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        var user = new Gson().fromJson(json, UserData.class);
        return user;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String white = rs.getString("whiteplayer");
        String black = rs.getString("blackplayer");
        String gamename = rs.getString("gamename");
        String game = rs.getString("game");
        System.out.println(game);
        var json = new Gson().fromJson(game, ChessGame.class);
        return new GameData(id,white, black, gamename, json);
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p){
                        ps.setInt(i + 1, p);
                    }
                    else if (param instanceof ChessGame p){
                        ps.setString(i + 1, p.toString());
                    }
                    else if (param == null){
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(400, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(100) NOT NULL,
              `password` varchar(100) NOT NULL,
              `email` varchar(100) NOT NULL,
              `json` varchar(246) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteplayer` varchar(100),
              `blackplayer` varchar(100),
              `gamename` varchar(100) NOT NULL,
              `game` longtext,
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auths (
              `authtoken` varchar(256) NOT NULL,
              `username` varchar(100),
              PRIMARY KEY (`authtoken`),
              INDEX(authtoken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(400, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
