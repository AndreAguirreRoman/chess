package service;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) != null){
            throw new DataAccessException(404, "Choose another username");
        }
        dataAccess.createUser(user);
        String authToken = generateToken();

        return dataAccess.createAuth(user, authToken);
    }

    public AuthData login(UserData userData) throws DataAccessException {
        UserData user = dataAccess.getUser(userData.username());
        if (user == null || !user.email().equals(userData.email()) ||
                !user.password().equals(userData.password())){
            throw new DataAccessException(404, "Invalid credentials");
        }
        AuthData userAuth = dataAccess.findAuthWithUser(user.username());
        if (userAuth != null){
            return userAuth;
        }
        String authToken = generateToken();
        return dataAccess.createAuth(user, authToken);
    }

    public void logout(String authToken) throws DataAccessException{
        dataAccess.deleteAuth(authToken);
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
