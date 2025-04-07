package server;

import com.google.gson.Gson;

import exception.DataException;
import results.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public RegisterResult addUser(RegisterRequest registerRequest) throws DataException {
        var path = "/user";
        return this.makeRequest("POST", path, registerRequest, RegisterResult.class, null);
    }

    public LoginResult loginUser(LoginRequest loginRequest) throws DataException {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, LoginResult.class, null);
    }

    public void logoutUser(String userAuth) throws DataException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, userAuth);
    }

    public GetGameResponse getGames(String authToken) throws DataException {
        var path = "/game";
        return this.makeRequest("GET", path, null, GetGameResponse.class, authToken);
    }

    public UpdateGameResponse updateGame(UpdateGameRequest updateGameRequest) throws DataException {
        var path = "/game";
        String authToken = updateGameRequest.authToken();
        return this.makeRequest("PUT", path, updateGameRequest,
                UpdateGameResponse.class, authToken);
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws DataException{
        var path = "/game";
        String authToken = createGameRequest.authToken();
        return this.makeRequest("POST", path, createGameRequest,
                CreateGameResponse.class, authToken);
    }

    public void deleteDatabase() throws DataException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }



    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authorization) throws DataException {
        try {

            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authorization != null){
                http.setRequestProperty("authorization", authorization);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            System.out.println("Sending request body: " + reqData);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            String errorMessage = "Unknown error";
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    errorMessage = new String(respErr.readAllBytes());
                }
            }

            throw new DataException(status, "other failure: " + errorMessage);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
