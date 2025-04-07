package exception;

import com.google.gson.Gson;

import java.util.Map;

public class DataException extends Exception {
    private final int code;
    public DataException(int code, String message) {
        super(message);
        this.code = code;
    }


    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }

    public int getStatusCode() {
        return code;
    }
}
