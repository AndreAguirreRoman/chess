package service;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import results.*;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException{
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException(400, "Error bad request");
        }
        if (dataAccess.getUser(user.username()) != null){
            throw new DataAccessException(403, "Error already taken");
        }

        UserData newUser = new UserData(0, user.username(), user.email(), user.password());

        dataAccess.createUser(newUser);
        String authToken = generateToken();
        AuthData newAuth = dataAccess.createAuth(newUser, authToken);
        return newAuth;
    }


    public LoginResult login(UserData request) throws DataAccessException {
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !BCrypt.checkpw(request.password(), user.password())){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        AuthData newUserAuth = dataAccess.createAuth(user, generateToken());
        LoginResult result = new LoginResult(newUserAuth.userName(), newUserAuth.authToken());
        return result;
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user = dataAccess.getUser(username);
        if (user == null) {
            throw new DataAccessException(500, "SOMETHING WENT WRONG");
        }
        return user;

    }
    public LogoutResponse logout(LogoutRequest request) throws DataAccessException{
        AuthData userAuth = dataAccess.getAuth(request.authToken());
        if (userAuth == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        dataAccess.deleteAuth(request.authToken());
        return new LogoutResponse(200);
    }


    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
