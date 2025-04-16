package websocket.commands;

import chess.ChessMove;

public class MakeMoveCmd extends UserGameCommand {
    private final ChessMove chessMove;
    private final String teamColor;

    public MakeMoveCmd(String authToken, int gameID, ChessMove chessMove, String teamColor){
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.teamColor = teamColor;
        this.chessMove = chessMove;
    }

    public ChessMove getChessMove(){
        return chessMove;
    }

    public String getTeamColor(){
        return teamColor;
    }

}
