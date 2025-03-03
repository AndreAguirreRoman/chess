package dataaccess.DAO;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public interface AuthDao {
    AuthData createAuth(UserData userData, String authToken) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
