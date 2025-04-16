package websocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage{
        private final ChessGame message;
        public LoadGame(ChessGame message){
            super(ServerMessageType.LOAD_GAME);
            this.message = message;
        }

        public ChessGame getMessage() {
            return message;
        }


}
