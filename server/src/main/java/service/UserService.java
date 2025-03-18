package service;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import results.*;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException{
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error bad request");
        }
        if (dataAccess.getUser(user.username()) != null){
            throw new DataAccessException("Error already taken");
        }

        UserData newUser = new UserData(0, user.username(), user.email(), user.password());

        dataAccess.createUser(newUser);
        String authToken = generateToken();
        AuthData newAuth = dataAccess.createAuth(newUser, authToken);
        return newAuth;
    }


    public LoginResult login(UserData request) throws DataAccessException {
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !user.password().equals(request.password())){
            throw new DataAccessException("Error: unauthorized");
        }
        AuthData newUserAuth = dataAccess.createAuth(user, generateToken());
        LoginResult result = new LoginResult(newUserAuth.userName(), newUserAuth.authToken());
        return result;
    }

    public LogoutResponse logout(LogoutRequest request) throws DataAccessException{
        AuthData userAuth = dataAccess.getAuth(request.authToken());
        if (userAuth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        dataAccess.deleteAuth(request.authToken());
        return new LogoutResponse(200);
    }


    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
