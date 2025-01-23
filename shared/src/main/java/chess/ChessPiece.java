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
                    break;
                case ROOK:
                    break;
                case BISHOP:
                    break;
                case QUEEN:
                    break;
                case KING:
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
