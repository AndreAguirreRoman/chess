package dataaccess.memory;

import dataaccess.DAO.AuthDao;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDao implements AuthDao {
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    public AuthData createAuth(UserData userData, String authToken) throws DataAccessException{
        for (AuthData existingToken : authTokens.values()){
            if (existingToken.userName().equals(userData.username())) {
                return existingToken;
            }
        }
        AuthData authData = new AuthData(generateToken(), userData.username());
        authTokens.put(authData.authToken(), authData);
        return authData;
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("No authorization!");
        }
        return authTokens.get(authToken);
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("No auth found!");
        }
        authTokens.remove(authToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }


}
