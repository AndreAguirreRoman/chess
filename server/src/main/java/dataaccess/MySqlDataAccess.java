package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
//import model.Pet;
//import model.PetType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;


import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }


    public UserData createUser(UserData user) throws DataAccessException{
        try {
            var statement = "INSERT INTO users (username, email, password, json) VALUES (?,?,?,?)";
            String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            UserData userHashed = new UserData(user.id(), user.username(), user.email(), hashedPassword);
            var json = new Gson().toJson(userHashed);
            executeUpdate(statement, user.username(), user.email(), hashedPassword, json);
        } catch (Exception e){
            throw new DataAccessException(400, e.getMessage());
        }
        return user;
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

    @Override
    public void clear() throws DataAccessException {

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
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(authToken, rs.getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auths where authtoken =?";
        executeUpdate(statement, authToken);
    }

    public GameData createGame(GameData gameData) throws DataAccessException {
        return null;
    }

    public GameData getGame(int gameId) throws DataAccessException {
        return null;
    }

    public Collection<GameData> getGames() {
        return List.of();
    }

    public void updateGame(int gameId, String authToken, String playerColor) throws DataAccessException {

    }

    /*

    public Collection<Pet> listPets() throws DataAccessException {
        var result = new ArrayList<Pet>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM pet";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readPet(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public void deletePet(Integer id) throws DataAccessException {
        var statement = "DELETE FROM pet WHERE id=?";
        executeUpdate(statement, id);
    }

    public void deleteAllPets() throws DataAccessException {
        var statement = "TRUNCATE pet";
        executeUpdate(statement);
    }


     */
    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        var user = new Gson().fromJson(json, UserData.class);
        return user;
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
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
            CREATE TABLE IF NOT EXISTS  users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(100) NOT NULL,
              `password` varchar(100) NOT NULL,
              `email` varchar(100) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `gamename` varchar(100) NOT NULL,
              `whiteplayer` varchar(100),
              `blackplayer` varchar(100),
              `game` varchar(512),
              PRIMARY KEY (`id`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  auths (
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
