package dataaccess;

import com.google.gson.Gson;

import java.util.Map;

public class DataAccessException extends Exception {

    public DataAccessException(String message) {
        super(message);
    }


    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }
}
