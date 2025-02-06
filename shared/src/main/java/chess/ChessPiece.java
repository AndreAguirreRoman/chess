package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor pieceColor;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> allowedMoves = new ArrayList<>();
        ChessPiece current = board.getPiece(myPosition);
        if (current != null){
            switch (current.getPieceType()){
                case PAWN:
                    allowedMoves.addAll(getPawnMoves(board, myPosition, current));
                    break;
                case KING:
                    allowedMoves.addAll(getKingMoves(board, myPosition, current));
                    break;
                case KNIGHT:
                    allowedMoves.addAll(getKnightMoves(board, myPosition, current));
                    break;
                case QUEEN:
                    allowedMoves.addAll(getQueenMoves(board, myPosition, current));
                    break;
                case BISHOP:
                    allowedMoves.addAll(getBishopMoves(board, myPosition, current));
                    break;
                case ROOK:
                    allowedMoves.addAll(getRookMoves(board, myPosition, current));
                    break;
            }
        }

        return allowedMoves;
    }

    public Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current){
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        ChessGame.TeamColor color = current.getTeamColor();

        int teamDirection = (color == ChessGame.TeamColor.WHITE)? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int rightCheck = myPosition.getColumn() + 1;
        int leftCheck = myPosition.getColumn() - 1;

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //moveforward
        ChessPiece frontPiece = board.getPiece(new ChessPosition(row + teamDirection, col));
        if (inBounds(row + teamDirection, col) && frontPiece == null){
            pawnMovesMaker(board, myPosition, pawnMoves, color, row, teamDirection, col, false);
        }

        pawnMovesMaker(board, myPosition, pawnMoves, color, row, teamDirection, rightCheck, true);
        pawnMovesMaker(board, myPosition, pawnMoves, color, row, teamDirection, leftCheck, true);

        if (inBounds(row + 2 * teamDirection, col)) {
            ChessPiece frontDoublePiece = board.getPiece(new ChessPosition(row + 2 * teamDirection, col));
            if (row == startRow && frontPiece == null && frontDoublePiece == null){
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row + 2 * teamDirection, col), null));
            }
        }


        return pawnMoves;
    }

    void pawnMovesMaker(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> pawnMoves,
                        ChessGame.TeamColor color, int row, int teamDirection, int colCheckDirection, boolean capture){

        int rowChecked = row + teamDirection;

        if (inBounds(rowChecked, colCheckDirection)){

            ChessPiece frontPiece = board.getPiece(new ChessPosition(rowChecked, colCheckDirection));
            int transformationRow = (color == ChessGame.TeamColor.WHITE)? 8 : 1;

            if (capture){
                if(frontPiece != null && frontPiece.getTeamColor() != color){
                    if (rowChecked == transformationRow){
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), PieceType.ROOK));
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), PieceType.KNIGHT));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), null));
                    }
                }
            } else {
                if(frontPiece == null){
                    if (rowChecked == transformationRow){
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), PieceType.ROOK));
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), PieceType.KNIGHT));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, new ChessPosition(rowChecked, colCheckDirection), null));
                    }
                }
            }
        }
    }



    public Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current){
        Collection<ChessMove> knightMoves = new ArrayList<>();
        ChessGame.TeamColor color = current.getTeamColor();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //UP
        kingMovesMaker(board, myPosition, row + 2, col + 1, color, knightMoves);
        kingMovesMaker(board, myPosition, row + 2, col - 1, color, knightMoves);

        //RIGHT
        kingMovesMaker(board, myPosition, row + 1, col + 2, color, knightMoves);
        kingMovesMaker(board, myPosition, row - 1, col + 2, color, knightMoves);


        //LEFT
        kingMovesMaker(board, myPosition, row + 1, col - 2, color, knightMoves);
        kingMovesMaker(board, myPosition, row - 1, col - 2, color, knightMoves);


        //DOWN
        kingMovesMaker(board, myPosition, row - 2, col + 1, color, knightMoves);
        kingMovesMaker(board, myPosition, row - 2, col - 1, color, knightMoves);


        return knightMoves;
    }

    public Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current) {
        Collection<ChessMove> bishopMoves = new ArrayList<>();
        ChessGame.TeamColor color = current.getTeamColor();

        bishopMovesMaker(board, myPosition, color, bishopMoves);
        return bishopMoves;
    }

    public Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current){
        Collection<ChessMove> queenMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = current.getTeamColor();
        bishopMovesMaker(board, myPosition, color, queenMoves);
        rookMovesMaker(board, myPosition, row, col, color, queenMoves);

        return queenMoves;

    }

    void bishopMovesMaker(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color, Collection<ChessMove> bishopMoves) {
        //Diagonal

        int rowUpRight = myPosition.getRow();
        int colUpRight = myPosition.getColumn();

        int rowUpLeft = myPosition.getRow();
        int colUpLeft = myPosition.getColumn();

        int rowDownLeft = myPosition.getRow();
        int colDownLeft = myPosition.getColumn();

        int rowDownRight = myPosition.getRow();
        int colDownRight = myPosition.getColumn();

        while (inBounds(rowUpRight + 1, colUpRight + 1)) {
            rowUpRight++;
            colUpRight++;
            ChessPiece frontCheck = board.getPiece(new ChessPosition(rowUpRight, colUpRight));
            if (frontCheck == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowUpRight, colUpRight), null));
            } else if (frontCheck.getTeamColor() != color) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowUpRight, colUpRight), null));
                break;
            } else {
                break;
            }
        }
        while (inBounds(rowUpLeft + 1, colUpLeft - 1 )) {
            rowUpLeft++;
            colUpLeft--;
            ChessPiece frontCheck = board.getPiece(new ChessPosition(rowUpLeft, colUpLeft));
            if (frontCheck == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowUpLeft, colUpLeft), null));
            } else if (frontCheck.getTeamColor() != color) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowUpLeft, colUpLeft), null));
                break;
            } else {
                break;
            }
        }
        while (inBounds(rowDownRight - 1, colDownRight + 1)) {
            rowDownRight--;
            colDownRight++;
            ChessPiece frontCheck = board.getPiece(new ChessPosition(rowDownRight, colDownRight));
            if (frontCheck == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowDownRight, colDownRight), null));
            } else if (frontCheck.getTeamColor() != color) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowDownRight, colDownRight), null));
                break;
            } else {
                break;
            }
        }
        while (inBounds(rowDownLeft - 1, colDownLeft - 1)) {
            rowDownLeft--;
            colDownLeft--;
            ChessPiece frontCheck = board.getPiece(new ChessPosition(rowDownLeft, colDownLeft));
            if (frontCheck == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowDownLeft, colDownLeft), null));
            } else if (frontCheck.getTeamColor() != color) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowDownLeft, colDownLeft), null));
                break;
            } else {
                break;
            }
        }
    }

    void rookMovesMaker(ChessBoard board, ChessPosition myPosition, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> rookMoves){
        //derecho
        int rowUp = myPosition.getRow();
        int colRight = myPosition.getColumn();
        int rowDown = myPosition.getRow();
        int colLeft = myPosition.getColumn();

        while (inBounds(rowUp + 1, col)){
            rowUp++;
            ChessPiece frontCheck = board.getPiece(new ChessPosition(rowUp, col));
            if (frontCheck == null){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowUp, col), null));
            } else if (frontCheck.getTeamColor() != color){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowUp, col), null));
                break;
            } else {
                break;
            }
        }
        while (inBounds(rowDown - 1, col)){
            rowDown --;
            ChessPiece frontCheck = board.getPiece(new ChessPosition(rowDown, col));
            if (frontCheck == null){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowDown, col), null));
            } else if (frontCheck.getTeamColor() != color){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowDown, col), null));
                break;
            } else {
                break;
            }
        }
        while (inBounds(row, colRight + 1)){
            colRight++;
            ChessPiece frontCheck = board.getPiece(new ChessPosition(row, colRight));
            if (frontCheck == null){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, colRight), null));
            } else if (frontCheck.getTeamColor() != color){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, colRight), null));
                break;
            } else {
                break;
            }
        }
        while (inBounds(row, colLeft - 1)){
            colLeft--;
            ChessPiece frontCheck = board.getPiece(new ChessPosition(row, colLeft));
            if (frontCheck == null){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, colLeft), null));
            } else if (frontCheck.getTeamColor() != color){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, colLeft), null));
                break;
            } else {
                break;
            }
        }
    }

    public Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current){
        Collection<ChessMove> rookMoves = new ArrayList<>();
        ChessGame.TeamColor color = current.getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        rookMovesMaker(board, myPosition, row, col, color, rookMoves);
        return rookMoves;
    }


    public Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current){
        Collection<ChessMove> kingMoves = new ArrayList<>();
        ChessGame.TeamColor color = current.getTeamColor();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        kingMovesMaker(board, myPosition, row + 1, col, color, kingMoves);
        kingMovesMaker(board, myPosition, row + 1, col + 1, color, kingMoves);
        kingMovesMaker(board, myPosition, row + 1, col - 1, color, kingMoves);
        kingMovesMaker(board, myPosition, row, col + 1, color, kingMoves);
        kingMovesMaker(board, myPosition, row, col - 1, color, kingMoves);
        kingMovesMaker(board, myPosition, row - 1, col, color, kingMoves);
        kingMovesMaker(board, myPosition, row - 1, col + 1, color, kingMoves);
        kingMovesMaker(board, myPosition, row - 1, col - 1, color, kingMoves);

        return kingMoves;
    }

    void kingMovesMaker(ChessBoard board, ChessPosition myPosition, int row, int col, ChessGame.TeamColor color, Collection<ChessMove> kingMoves){
        if (inBounds(row, col)){
            ChessPiece pieceCheck = board.getPiece(new ChessPosition(row, col));
            if (pieceCheck != null && pieceCheck.getTeamColor() != color){
                kingMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            if (pieceCheck == null) {
                kingMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
        }
    }



    boolean inBounds(int row, int col){
        return row >= 1 && row < 9 && col >= 1 && col < 9;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", pieceColor=" + pieceColor +
                '}';
    }
}
