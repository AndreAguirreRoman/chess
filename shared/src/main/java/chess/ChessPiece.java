package chess;

import java.util.ArrayList;
import java.util.Collection;

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
            if (inBounds((myPosition.getRow() + teamDirection), myPosition.getColumn() + 1)) {
                System.out.println(myPosition.getRow());
                System.out.println(myPosition.getColumn());
                ChessPiece rightFrontPiece = board.getPiece(new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() + 1));

                if (rightFrontPiece != null && rightFrontPiece.getTeamColor() != team) {
                    if (myPosition.getRow() + 1 == 8 || myPosition.getRow() - 1 == 1) {
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() + 1), PieceType.QUEEN));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() + 1), PieceType.ROOK));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() + 1), PieceType.KNIGHT));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() + 1), PieceType.BISHOP));
                    } else {
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() + 1), current.type));
                    }
                }
            }
            if (inBounds(myPosition.getRow() + teamDirection, myPosition.getColumn() - 1)) {
                ChessPiece leftFrontPiece = board.getPiece(new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() - 1 ));
                if (leftFrontPiece != null && leftFrontPiece.getTeamColor() != team) {
                    if (myPosition.getRow() + 1 == 8 || myPosition.getRow() - 1 == 1) {
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() - 1), PieceType.QUEEN));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() - 1), PieceType.ROOK));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() - 1), PieceType.KNIGHT));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() - 1), PieceType.BISHOP));
                    } else {
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn() - 1), current.type));
                    }
                }
            }

            if (inBounds((myPosition.getRow() + teamDirection), myPosition.getColumn())) {
                ChessPiece frontPiece = board.getPiece(new ChessPosition(myPosition.getRow() + teamDirection , myPosition.getColumn()));
                if (frontPiece == null) {
                    if (myPosition.getRow() + 1 == 8) {
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn()), PieceType.QUEEN));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn()), PieceType.ROOK));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn()), PieceType.KNIGHT));
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn()), PieceType.BISHOP));
                    } else {
                        pawnMovesList.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + teamDirection, myPosition.getColumn()), null));
                    }
                }

            }
        return pawnMovesList;
    }

    private boolean inBounds(int row, int column) {
        if (row < 0)
    }

}
