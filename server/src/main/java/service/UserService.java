package service;
import dataaccess.DataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData createUser(UserData user) throws ResponseException {
        if (dataAccess.getUser(user.username()) != null){
            throw new ResponseException("Choose another username");
        }
        dataAccess.createUser(user);
        String authToken = generateToken();

        return dataAccess.createAuth(user, authToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
