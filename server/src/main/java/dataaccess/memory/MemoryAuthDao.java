package dataaccess.memory;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDao {
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    public AuthData createAuth(UserData userData) throws DataAccessException{
        if (authTokens.containsKey(userData.id())){
            throw new DataAccessException("Sus...");
        }
        AuthData authData = new AuthData(generateToken(), userData.username());
        authTokens.put(authData.authToken(), authData);
        return authData;
    }
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
