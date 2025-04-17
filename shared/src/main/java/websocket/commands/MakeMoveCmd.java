package websocket.commands;

import chess.ChessMove;

public class MakeMoveCmd extends UserGameCommand {
    private final ChessMove move;
    private final String teamColor;

    public MakeMoveCmd(String authToken, int gameID, ChessMove move, String teamColor){
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.teamColor = teamColor;
    }

    public ChessMove getMove(){
        return move;
    }

    public String getTeamColor(){
        return teamColor;
    }

}
