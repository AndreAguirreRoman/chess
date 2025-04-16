package websocket.commands;

import chess.ChessMove;

public class MakeMoveCmd extends UserGameCommand {
    private final ChessMove chessMove;

    public MakeMoveCmd(String authToken, int gameID, ChessMove chessMove){
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.chessMove = chessMove;
    }

    public ChessMove getChessMove(){
        return chessMove;
    }

}
