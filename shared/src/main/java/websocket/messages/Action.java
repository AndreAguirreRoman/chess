package websocket.messages;

import com.google.gson.Gson;

public record Action(Type type, String username) {
    public enum Type{
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public String toString(){
        return new Gson().toJson(this);
    }

}
