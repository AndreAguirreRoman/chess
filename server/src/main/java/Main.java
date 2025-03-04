import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.Server;


public class Main {
    public static void main(String[] args) {

        DataAccess dataAccess = new MemoryDataAccess();
        Server server = new Server(dataAccess);
        server.run(8080);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

    }


}