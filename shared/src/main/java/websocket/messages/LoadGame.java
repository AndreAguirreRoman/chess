package websocket.messages;

public class LoadGame extends ServerMessage{
        private final String message;
        public LoadGame(String message){
            super(ServerMessageType.LOAD_GAME);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }


}
