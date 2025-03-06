package dataaccess;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DataAccessException extends Exception {
    final private int statusCode;

    public DataAccessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }
}
