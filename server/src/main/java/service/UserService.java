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
            throw new DataAccessException(400, "bad request");
        }
        if (dataAccess.getUser(user.username()) != null){
            throw new DataAccessException(403, "already taken");
        }

        UserData newUser = new UserData(0, user.username(), user.email(), user.password());
        dataAccess.createUser(newUser);
        String authToken = generateToken();
        AuthData newAuth = dataAccess.createAuth(user, authToken);
        return newAuth;
    }


    public AuthData getUserAuth(String userAuth) throws DataAccessException{
        AuthData user = dataAccess.getAuth(userAuth);
        return user;
    }


    public LoginResult login(UserData request) throws DataAccessException {
        System.out.println("Login attempt for user: " + request.username());
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !user.password().equals(request.password())){
            throw new DataAccessException(401, "unauthorized");
        }
        AuthData userAuth = dataAccess.findAuthWithUser(user.username());
        if (userAuth != null){
            return new LoginResult(userAuth.userName(), userAuth.authToken());
        }
        String authToken = generateToken();
        AuthData newUserAuth = dataAccess.createAuth(user, authToken);
        LoginResult result = new LoginResult(newUserAuth.userName(), newUserAuth.authToken());
        System.out.println("Returning LoginResult: " + result);
        return result;
    }

    public LogoutResponse logout(LogoutRequest request) throws DataAccessException{
        AuthData userAuth = dataAccess.getAuth(request.authToken());
        if (userAuth == null) {
            throw new DataAccessException(401, "unauthorized");
        }
        dataAccess.deleteAuth(request.authToken());
        return new LogoutResponse(200);
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
