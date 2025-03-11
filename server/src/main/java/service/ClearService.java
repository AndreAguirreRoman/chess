package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public void deleteUser(String username) throws DataAccessException{
        AuthData user = dataAccess.findAuthWithUser(username);
        if (user == null){
            throw new DataAccessException(400, "No user to delete");
        }
        dataAccess.deleteAuth(user.authToken());
        dataAccess.deleteUser(user.userName());
    }

    public void deleteUserList() {
        dataAccess.clearUserList();;
    }

    public void deleteEverything() throws DataAccessException {
        dataAccess.clear();
    }
}
