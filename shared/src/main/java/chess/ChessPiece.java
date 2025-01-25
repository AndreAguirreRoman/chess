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

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
            switch (current.getPieceType()) {
                case PAWN:
                    allowedMoves.addAll(getPawnMoves(board, myPosition, current));
                    break;
                case KNIGHT:
                    allowedMoves.addAll(getKnightMove(board, myPosition, current));
                    break;
                case ROOK:
                    allowedMoves.addAll(getRookMoves(board, myPosition, current));
                    break;
                case BISHOP:
                    allowedMoves.addAll(getBishopMoves(board, myPosition, current));
                    break;
                case QUEEN:
                    allowedMoves.addAll(getQueenMoves(board, myPosition, current));
                    break;
                case KING:
                    allowedMoves.addAll(getKingMoves(board, myPosition, current));
                    break;
            }
        }
        return allowedMoves;
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current) {

        Collection<ChessMove> pawnMovesList = new ArrayList<>();
        ChessGame.TeamColor team = current.getTeamColor();
        int teamDirection = (team == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int transformationRow = (team == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int rightCheck = (myPosition.getColumn() + 1);
        int leftCheck = (myPosition.getColumn() - 1);
        int row = (myPosition.getRow());
        int column = (myPosition.getColumn());
        boolean transformationChecker = (team == ChessGame.TeamColor.WHITE ? row + 1 : row - 1) == transformationRow;

        if (inBounds((row + teamDirection), rightCheck)) {

                ChessPiece rightFrontPiece = board.getPiece(new ChessPosition(row + teamDirection, rightCheck));
                if (rightFrontPiece != null && rightFrontPiece.getTeamColor() != team) {
                        if (transformationChecker) {
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, rightCheck), PieceType.QUEEN));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, rightCheck), PieceType.ROOK));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, rightCheck), PieceType.KNIGHT));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, rightCheck), PieceType.BISHOP));
                        } else {
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, rightCheck), null));
                        }
                }
            }
            if (inBounds(row + teamDirection, leftCheck)) {
                ChessPiece leftFrontPiece = board.getPiece(new ChessPosition(row + teamDirection, leftCheck ));
                if (leftFrontPiece != null && leftFrontPiece.getTeamColor() != team) {
                    if (transformationChecker) {
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, leftCheck), PieceType.QUEEN));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, leftCheck), PieceType.ROOK));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, leftCheck), PieceType.KNIGHT));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, leftCheck), PieceType.BISHOP));
                        } else {
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, leftCheck), null));
                        }

                }
            }

            if (inBounds((row + teamDirection), column)) {
                ChessPiece frontPiece = board.getPiece(new ChessPosition(row + teamDirection , column));
                if (frontPiece == null) {
                    if (transformationChecker) {
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, column), PieceType.QUEEN));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, column), PieceType.ROOK));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, column), PieceType.KNIGHT));
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, column), PieceType.BISHOP));
                        } else {
                            pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + teamDirection, column), null));
                        }
                }

            }

            if (current.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (inBounds(row + 1, column)) {
                    ChessPiece frontPieceWhite = board.getPiece(new ChessPosition(row + 1 , column)); // This might be creating the issues
                    if (inBounds(row + 2, column)) {
                        ChessPiece frontDoublePieceWhite = board.getPiece(new ChessPosition(row + 2, column)); // This might be creating the issues
                        if (row == 2) {
                            if (frontPieceWhite == null && frontDoublePieceWhite == null) {
                                pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row + 2, column), null));
                            }
                        }
                    }

                }
            } else if (current.getTeamColor() == ChessGame.TeamColor.BLACK){
                if (inBounds(row - 1, column)) {
                    ChessPiece frontPieceBlack = board.getPiece(new ChessPosition(row - 1 , column)); // This might be creating the issues
                    if (inBounds(row - 2, column)) {
                        ChessPiece frontDoublePieceBlack = board.getPiece(new ChessPosition(row - 2, column)); // This might be creating the issues
                        if (row == 7) {
                            if (frontPieceBlack == null && frontDoublePieceBlack == null) {
                                pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(row - 2, column), null));
                            }
                        }
                    }
                }
            }


        return pawnMovesList;
    }

    private Collection<ChessMove> getKnightMove(ChessBoard board, ChessPosition myPosition, ChessPiece current) {
        Collection<ChessMove> knightMovesList = new ArrayList<>();
        ChessGame.TeamColor team = current.getTeamColor();
        int row = (myPosition.getRow());
        int column = (myPosition.getColumn());

        // going up
        if (inBounds(row + 2, column) ) {
            if (inBounds(row + 2, column + 1)){
                ChessPiece piece = board.getPiece(new ChessPosition(row + 2, column + 1));
                if (piece == null || piece.getTeamColor() != team) {
                knightMovesList.add(new ChessMove(myPosition, new ChessPosition(row + 2, column + 1), null));
                }
            }
            if (inBounds(row + 2, column - 1)){
                ChessPiece piece = board.getPiece(new ChessPosition(row + 2, column - 1));
                if (piece == null || piece.getTeamColor() != team) {
                    knightMovesList.add(new ChessMove(myPosition, new ChessPosition(row + 2, column - 1), null));
                }
            }
        }
        // Going down
        if (inBounds(row - 2, column)) {
            if (inBounds(row - 2, column + 1)) {
                ChessPiece piece = board.getPiece(new ChessPosition(row - 2, column + 1));
                if (piece == null || piece.getTeamColor() != team) {
                    knightMovesList.add(new ChessMove(myPosition, new ChessPosition(row - 2, column + 1), null));
                }
            }
            if (inBounds(row - 2, column - 1)) {
                ChessPiece piece = board.getPiece(new ChessPosition(row - 2, column - 1));
                if (piece == null || piece.getTeamColor() != team) {
                    knightMovesList.add(new ChessMove(myPosition, new ChessPosition(row - 2, column - 1), null));

                }
            }
        }
        //Going right
        if (inBounds(row, column + 2)) {
            if (inBounds(row - 1, column + 2)){
                ChessPiece piece = board.getPiece(new ChessPosition(row - 1, column + 2));
                if (piece == null || piece.getTeamColor() != team) {
                knightMovesList.add(new ChessMove(myPosition, new ChessPosition(row - 1, column + 2), null));
                }
            }
            if (inBounds(row + 1, column + 2)) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, column + 2));
                if (piece == null || piece.getTeamColor() != team){
                    knightMovesList.add(new ChessMove(myPosition, new ChessPosition(row + 1, column + 2), null));
                }
            }
        }
        //Going left
        if (inBounds(row, column - 2)) {
            if (inBounds(row - 1, column - 2)) {
                ChessPiece piece = board.getPiece(new ChessPosition(row - 1, column - 2));
                if (piece == null || piece.getTeamColor() != team) {
                    knightMovesList.add(new ChessMove(myPosition, new ChessPosition(row - 1, column - 2), null));
                }
            }
            if (inBounds(row + 1, column - 2)) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, column - 2));
                if (piece == null || piece.getTeamColor() != team) {
                    knightMovesList.add(new ChessMove(myPosition, new ChessPosition(row + 1, column - 2), null));
                }
            }
        }

        return knightMovesList;
    }


    private void rookMovesMaker(Collection<ChessMove> rookMoves, ChessBoard board, ChessPosition myPosition, int row, int column, ChessGame.TeamColor team){
        int rowUp = row;
        int rowDown = row;
        int columnUp = column;
        int columnDown = column;

        while (inBounds(rowUp + 1, column)) {
            rowUp++;
            ChessPiece frontPiece = board.getPiece(new ChessPosition(rowUp, column));
            if (frontPiece == null) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowUp, column), null));
            } else if ((frontPiece.getTeamColor() != team)){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowUp, column), null));
                break;
            } else {
                break;
            }
        }

        while (inBounds(rowDown - 1, column)) {
            rowDown--;
            ChessPiece frontPiece = board.getPiece(new ChessPosition(rowDown, column));
            if (frontPiece == null) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowDown, column), null));
            } else if ((frontPiece.getTeamColor() != team)){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(rowDown, column), null));
                break;
            } else {
                break;
            }
        }

        while (inBounds(row, columnUp + 1)) {
            columnUp++;
            ChessPiece frontPiece = board.getPiece(new ChessPosition(row, columnUp));
            if (frontPiece == null) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, columnUp), null));
            } else if ((frontPiece.getTeamColor() != team)){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, columnUp), null));
                break;
            } else {
                break;
            }
        }
        while (inBounds(row, columnDown - 1)) {

            columnDown--;
            ChessPiece frontPiece = board.getPiece(new ChessPosition(row, columnDown));
            if (frontPiece == null) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, columnDown), null));
            } else if ((frontPiece.getTeamColor() != team)){
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, columnDown), null));
                break;
            } else {
                break;
            }
        }
    }
    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current) {
        Collection<ChessMove> rookMoves = new ArrayList<>();
        ChessGame.TeamColor team = current.getTeamColor();
        int row = (myPosition.getRow());
        int column = (myPosition.getColumn());

        rookMovesMaker(rookMoves, board, myPosition, row, column, team);

        return rookMoves;
    }

    private void bishopMovesMaker(Collection<ChessMove> bishopMoves, ChessBoard board, ChessPosition myPosition, int row, int column, ChessGame.TeamColor team) {

        int rowUpRight = row;
        int columnUpRight = column;

        int rowDownRight = row;
        int columnDownRight = column;

        int rowDownLeft = row;
        int columnDownLeft = column;

        int columnUpLeft = column;
        int rowUpLeft = row;

        //DIagonal Up-right
        while (inBounds(rowUpRight + 1, columnUpRight + 1)) {
            rowUpRight++;
            columnUpRight++;
            ChessPiece frontPiece = board.getPiece(new ChessPosition(rowUpRight, columnUpRight));
            if (frontPiece == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowUpRight, columnUpRight), null));
            } else if ((frontPiece.getTeamColor() != team)){
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowUpRight, columnUpRight), null));
                break;
            } else {
                break;
            }
        }
        //DIagonal up-left
        while (inBounds(rowUpLeft + 1, columnUpLeft - 1)) {
            rowUpLeft++;
            columnUpLeft--;
            ChessPiece frontPiece = board.getPiece(new ChessPosition(rowUpLeft, columnUpLeft));
            if (frontPiece == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowUpLeft, columnUpLeft), null));
            } else if ((frontPiece.getTeamColor() != team)){
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowUpLeft, columnUpLeft), null));
                break;
            } else {
                break;
            }
        }

        //DIagonal Down-right

        while (inBounds(rowDownRight - 1, columnDownRight + 1)) {
            columnDownRight++;
            rowDownRight--;
            ChessPiece frontPiece = board.getPiece(new ChessPosition(rowDownRight, columnDownRight));
            if (frontPiece == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowDownRight, columnDownRight), null));
            } else if ((frontPiece.getTeamColor() != team)){
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowDownRight, columnDownRight), null));
                break;
            } else {
                break;
            }
        }

        //Diagonal Down-left
        while (inBounds(rowDownLeft - 1, columnDownLeft - 1)) {
            columnDownLeft--;
            rowDownLeft--;
            ChessPiece frontPiece = board.getPiece(new ChessPosition(rowDownLeft, columnDownLeft));
            if (frontPiece == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowDownLeft, columnDownLeft), null));
            } else if ((frontPiece.getTeamColor() != team)){
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(rowDownLeft, columnDownLeft), null));
                break;
            } else {
                break;
            }
        }


    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current) {
        Collection<ChessMove> bishopMoves = new ArrayList<>();
        ChessGame.TeamColor team = current.getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        bishopMovesMaker(bishopMoves, board, myPosition, row, col, team);

        return bishopMoves;
    }

    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current) {
        Collection<ChessMove> queenMoves = new ArrayList<>();
        ChessGame.TeamColor team = current.getTeamColor()
;        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        bishopMovesMaker(queenMoves, board, myPosition, row, column, team);
        rookMovesMaker(queenMoves, board, myPosition, row, column, team);
        return queenMoves;
    }



    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition myPosition, ChessPiece current) {
        Collection<ChessMove> kingMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        ChessGame.TeamColor team = current.getTeamColor();

        kingMovesMaker(kingMoves, board, myPosition, row + 1, column, team);
        kingMovesMaker(kingMoves, board, myPosition, row + 1, column + 1, team);
        kingMovesMaker(kingMoves, board, myPosition, row + 1, column - 1, team);
        kingMovesMaker(kingMoves, board, myPosition, row - 1, column, team);
        kingMovesMaker(kingMoves, board, myPosition, row - 1, column + 1, team);
        kingMovesMaker(kingMoves, board, myPosition, row - 1, column - 1, team);
        kingMovesMaker(kingMoves, board, myPosition, row, column + 1, team);
        kingMovesMaker(kingMoves, board, myPosition, row, column - 1, team);

        return kingMoves;
    }

    private void kingMovesMaker(Collection<ChessMove> kingMovesList, ChessBoard board, ChessPosition myPosition, int row, int column, ChessGame.TeamColor team) {
        if (inBounds(row, column)) {
            ChessPiece piece = board.getPiece(new ChessPosition(row, column));
            if (piece == null || piece.getTeamColor() != team){
                kingMovesList.add(new ChessMove(myPosition, new ChessPosition(row, column), null));
            }
        }
    }

    private boolean inBounds(int row, int column) {
        return row >= 1 && row < 9 && column >= 1 && column < 9;
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
}
