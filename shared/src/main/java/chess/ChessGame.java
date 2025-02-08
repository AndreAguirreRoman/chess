package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamColor;
    private ChessBoard board;
    private ChessPosition whiteKing;
    private ChessPosition blackKing;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamColor = TeamColor.WHITE;

        this.whiteKing = new ChessPosition(1,5);
        this.blackKing = new ChessPosition(8,5);

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece currentPiece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();


        if (currentPiece != null){
            TeamColor teamColor = currentPiece.getTeamColor();
            Collection<ChessMove> pieceMove = currentPiece.pieceMoves(board, startPosition);
            for (ChessMove move : pieceMove){
                ChessPiece destination = board.getPiece(move.getEndPosition());

                if (destination == null || board.getPiece(move.getEndPosition()).getTeamColor() != teamColor){
                    validMoves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece()));
                } else if (destination.getTeamColor() == teamColor) {
                    continue;
                }
            }
        } else {
            return validMoves;
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movePiece = this.board.getPiece(move.getStartPosition());
        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        TeamColor team = getTeamTurn();


        if (movePiece == null || team != movePiece.getTeamColor() || !possibleMoves.contains(move) ){
            throw new InvalidMoveException("Not valid move");
        }

        if (movePiece.getPieceType() == ChessPiece.PieceType.KING){
            if (movePiece.getTeamColor() == TeamColor.WHITE){
                whiteKing = move.getEndPosition();
            } else {
                blackKing = move.getEndPosition();
            }
        }



        ChessPiece checkPiece = this.board.getPiece(move.getEndPosition());
        if (checkPiece != null && checkPiece.getTeamColor() != team){
            this.board.addPiece(move.getEndPosition(), movePiece);
            this.board.addPiece(move.getStartPosition(),null);
        }
        if (checkPiece == null){
            this.board.addPiece(move.getEndPosition(), movePiece);
            this.board.addPiece(move.getStartPosition(),null);
        }
        if (movePiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int promotionRow = (movePiece.getTeamColor() == TeamColor.WHITE) ? 8 : 1;

            if (move.getEndPosition().getRow() == promotionRow) {
                this.board.addPiece(move.getEndPosition(), new ChessPiece(movePiece.getTeamColor(), move.getPromotionPiece()));
                this.board.addPiece(move.getStartPosition(), null);
                return;
            }
        }

        if (this.isInCheck(team)){
            throw new InvalidMoveException("Not the best move");
        }
        if (this.isInCheckmate(team)){
            throw new InvalidMoveException("Game over");
        }
        if (this.isInStalemate(team)){
            throw new InvalidMoveException("Stalemate bud");

        }
        teamColor = (team == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);

    }


    //asdasdasddas

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingColor = (teamColor == TeamColor.WHITE) ? whiteKing : blackKing;
        ChessPiece currentPiece;
        ChessPosition currentPosition;
        for (int x = 1; x < 9; x++){
            for (int y = 1; y < 9; y++){
                currentPosition = new ChessPosition(x,y);
                currentPiece = board.getPiece(new ChessPosition(x,y));


                if (currentPiece != null && teamColor != currentPiece.getTeamColor()){
                    if (currentPiece.pieceMoves(board, currentPosition).contains(new ChessMove(currentPosition, kingColor, null))){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPiece currentPiece;
        ChessPosition currentPosition;
        ChessPosition kingColor = (teamColor == TeamColor.WHITE) ? whiteKing : blackKing;

        if(!isInCheck(teamColor)){
            return false;
        }
        for (int x = 1; x < 9; x++){
            for (int y = 1; y < 9; y++){
                currentPosition = new ChessPosition(x,y);
                currentPiece = board.getPiece(new ChessPosition(x,y));

                if (currentPiece != null && teamColor == currentPiece.getTeamColor()){
                    if (!this.validMoves(currentPosition).isEmpty()){
                        System.out.println("Move available for: " + currentPosition);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPiece currentPiece;
        ChessPosition currentPosition;

        if (this.getTeamTurn() == teamColor) {
            for (int x = 1; x < 9; x++) {
                for (int y = 1; y < 9; y++) {
                    currentPosition = new ChessPosition(x, y);
                    currentPiece = board.getPiece(new ChessPosition(x, y));
                    if (currentPiece != null && teamColor == currentPiece.getTeamColor()) {


                        if (!this.validMoves(currentPosition).isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamColor == chessGame.teamColor && Objects.equals(board, chessGame.board) && Objects.equals(whiteKing, chessGame.whiteKing) && Objects.equals(blackKing, chessGame.blackKing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, board, whiteKing, blackKing);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamColor=" + teamColor +
                ", board=" + board +
                ", whiteKing=" + whiteKing +
                ", blackKing=" + blackKing +
                '}';
    }
}
