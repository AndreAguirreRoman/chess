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

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (dataAccess.getUser(request.userName()) != null){
            throw new DataAccessException(404, "Choose another username");
        }
        UserData user = new UserData(0, request.userName(), request.email(), request.password());

        dataAccess.createUser(user);
        String authToken = generateToken();
        AuthData authData = dataAccess.createAuth(user, authToken);
        return new RegisterResult(authData.userName(), authData.authToken(), 200);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = dataAccess.getUser(request.userName());
        if (user == null || !user.password().equals(request.password())){
            throw new DataAccessException(404, "Invalid credentials");
        }
        AuthData userAuth = dataAccess.findAuthWithUser(user.username());
        if (userAuth != null){
            return new LoginResult(userAuth.userName(), userAuth.authToken(), 200);
        }
        String authToken = generateToken();
        AuthData newUserAuth = dataAccess.createAuth(user, authToken);
        return new LoginResult(newUserAuth.userName(), newUserAuth.authToken(), 200);
    }

    public LogoutResponse logout(LogoutRequest request) throws DataAccessException{
        dataAccess.deleteAuth(request.authToken());
        return new LogoutResponse(200);
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
