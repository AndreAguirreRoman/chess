package dataaccess.memory;

import dataaccess.DAO.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> usersList = new HashMap<>();
    private int nextId = 1;

    public UserData createUser(UserData user) throws DataAccessException {
        if (usersList.containsKey(user.username())) {
            throw new DataAccessException("User Already Exists!");
        }
        user = new UserData(nextId++, user.username(), user.email(), user.password());
        usersList.put(user.username(), user);
        return user;
    }

    public UserData getUser(String username){
        return usersList.get(username);
    }

    public void deleteUser(String username) throws DataAccessException{
        if (!usersList.containsKey(username)){
            throw new DataAccessException("User not found!");
        }
        usersList.remove(username);
    }

    public void clear(){
        usersList.clear();
    }
}
