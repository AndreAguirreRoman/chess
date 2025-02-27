package dataaccess.DAO;

import dataaccess.DataAccessException;
import model.UserData;

public interface UserDAO {
    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
    void deleteUser(String username) throws DataAccessException;
    void clear();

}
