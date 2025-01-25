package chess;

import java.util.Arrays;
import java.util.Objects;

public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                squares[r][c] = null;
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1 ][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                squares[r][c] = null;
            }
        }
        for (int i = 0; i < 8; i++){
            chessPieceSetter(squares, 1, i, "White","pawn");
        }
        for (int i = 0; i < 8; i++) {
            chessPieceSetter(squares, 6, i, "black","pawn");
        }
        chessPieceSetter(squares, 0, 0, "white", "rook");
        chessPieceSetter(squares, 0, 1, "white", "knight");
        chessPieceSetter(squares, 0, 2, "white", "bishop");
        chessPieceSetter(squares, 0, 3, "white", "queen");
        chessPieceSetter(squares, 0, 4, "white", "king");
        chessPieceSetter(squares, 0, 5, "white", "bishop");
        chessPieceSetter(squares, 0, 6, "white", "knight");
        chessPieceSetter(squares, 0, 7, "white", "rook");


        chessPieceSetter(squares, 7, 0, "black", "rook");
        chessPieceSetter(squares, 7, 1, "black", "knight");
        chessPieceSetter(squares, 7, 2, "black", "bishop");
        chessPieceSetter(squares, 7, 3, "black", "queen");
        chessPieceSetter(squares, 7, 4, "black", "king");
        chessPieceSetter(squares, 7, 5, "black", "bishop");
        chessPieceSetter(squares, 7, 6, "black", "knight");
        chessPieceSetter(squares, 7, 7, "black", "rook");

        //:)


    }

    void chessPieceSetter(ChessPiece[][] squares, int row, int column, String teamColor, String pieceType){
        ChessGame.TeamColor team = ChessGame.TeamColor.valueOf(teamColor.toUpperCase());
        ChessPiece.PieceType piece = ChessPiece.PieceType.valueOf(pieceType.toUpperCase());

        squares[row][column] = new ChessPiece(team, piece);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
