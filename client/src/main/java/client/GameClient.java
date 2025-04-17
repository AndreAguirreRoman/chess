package client;
import chess.*;

import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;



import exception.DataException;

import results.UpdateGameRequest;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.*;

import static java.lang.Integer.parseInt;


public class GameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;

    String user = null;
    String token = null;
    String teamColor = "observer";
    boolean inCheckMate = false;
    int gameID = 0;
    boolean inGame = true;
    boolean observer = false;
    boolean gameOver = false;
    private WebSocketFacade ws;
    private ChessGame chessGame;

    public GameClient(String serverUrl, NotificationHandler notificationHandler){
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input, String username, String authToken, String teamColor,
                       boolean observer, boolean inGame, int gameID, ChessGame chessGame) throws DataException {
        user = username;
        token = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;
        this.inGame = inGame;
        this.observer = observer;
        this.chessGame = chessGame;
        boolean gameOver = chessGame.getGameOVer();

        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
            case "help" -> help();
            case "quit" -> "quit";
            case "board" -> drawBoard(teamColor, observer, null, this.chessGame);
            case "leave" -> exitGame();
            case "resign" -> resign();
            case "legalmoves" -> highlight(params);
            case "makemove" -> makeMove(params);
            default -> help();
        };
    }

    public String makeMove(String... params) throws DataException {
        if (params.length == 2) {
            if (chessGame.getGameOVer()) {
                return "Game is over. No more moves";
            }
            if (this.gameOver){
                System.out.println("GAME OVER");
            }
            if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) || chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                chessGame.setGameOver(true);
            }
            try {

                this.ws = new WebSocketFacade(serverUrl,notificationHandler);
                String posStart = params[0];
                String posEnd = params[1];
                if (!validPositions().contains(posStart) || !validPositions().contains(posEnd)){
                    return ("Check your positions! One is not inside the board.");
                }
                ChessPosition posStartTransformed = transformationPosition(LETTER_TO_NUM, posStart);
                ChessPosition posEndTransformed = transformationPosition(LETTER_TO_NUM, posEnd);
                ChessMove chessMove = new ChessMove(posStartTransformed, posEndTransformed, null);

                ChessPiece piece = this.chessGame.getBoard().getPiece(chessMove.getStartPosition());
                System.out.println("Piece at start: " + piece);
                System.out.println("Trying move: " + posStart + " to " + posEnd);
                System.out.println("Your team color: " + this.teamColor);
                System.out.println("Turn: " + this.chessGame.getTeamTurn());
                System.out.println("Trying to move piece at " + chessMove.getStartPosition());
                System.out.println("Trying to move piece to " + chessMove.getEndPosition());

                try {
                    ws.makeMove(this.token, this.gameID, chessMove, this.teamColor);

                } catch (DataException e) {
                    return "Move made! From: " + posStart + " to: " + posEnd;
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return "Invalid format, expected: makemove <startposition> <endposition>";
        }
        return "";
    }

    private static final Map<Character, Integer> LETTER_TO_NUM = Map.of(
            'a', 1, 'b',2, 'c', 3, 'd', 4, 'e', 5,
            'f', 6, 'g', 7, 'h', 8
    );

    ChessPosition transformationPosition(Map<Character, Integer> list, String pos) {
        char colToConvert = pos.toLowerCase().strip().charAt(0);
        int colInt = list.get(colToConvert);

        int row = parseInt(String.valueOf(pos.toLowerCase().strip().charAt(1)));

        ChessPosition position = new ChessPosition(row ,colInt);
        return position;
    }

    private static List<String> validPositions(){
        List<String> positions = new ArrayList<>();
        List<String> letters = List.of("a","b","c","d","e","f","g", "h");
        for (String l : letters) {
            for (int i = 1; i <= 8; i++){
                positions.add(l+i);
            }
        }
        return positions;
    }

    public String drawBoard(String teamColor, boolean observer, Collection<ChessPosition> allowedMoves, ChessGame chessGame) throws DataException {

        if (observer) {
            teamColor = "white";
        }
        StringBuilder sb = new StringBuilder();
        String letters = (teamColor.equals("white") ? ("   a  b  c  d  e  f  g  h\n") : ("   h  g  f  e  d  c  b  a\n"));
        sb.append(letters);

        List<Integer> rowDecider = new ArrayList<>();
        if (teamColor.equals("white")) {
            rowDecider.add(7);
            rowDecider.add(-1);
            rowDecider.add(-1);
            rowDecider.add(0);
            rowDecider.add(8);
            rowDecider.add(1);
        } else {
            rowDecider.add(0);
            rowDecider.add(8);
            rowDecider.add(1);
            rowDecider.add(7);
            rowDecider.add(-1);
            rowDecider.add(-1);

        }
        for (int row = rowDecider.get(0); row != rowDecider.get(1); row += rowDecider.get(2)) {
            sb.append(row + 1).append(" ");
            for (int col = rowDecider.get(3); col != rowDecider.get(4); col += rowDecider.get(5)) {
                ChessPiece piece = chessGame.getBoard().getPiece(new ChessPosition(row + 1, col + 1));
                boolean highlight = (allowedMoves != null && allowedMoves.contains(new ChessPosition(row + 1, col + 1)));
                String bg = highlight ? EscapeSequences.SET_BG_COLOR_YELLOW : ((row + col) % 2 == 0)
                        ? EscapeSequences.SET_BG_COLOR_WHITE
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                if (piece != null) {
                    sb.append(bg).append(iconSetter(piece.getPieceType(), piece.getTeamColor())).append(EscapeSequences.RESET_BG_COLOR);
                } else {
                    sb.append(bg).append("   ").append(EscapeSequences.RESET_BG_COLOR);
                }
            }

            sb.append("\n");
        }

        sb.append(letters);

        return sb.toString();
    }


    public String iconSetter(ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor){
        ChessGame.TeamColor colorDecider = (teamColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
        switch (pieceType){
            case KING -> {
                return (colorDecider == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING);
            }
            case QUEEN -> {
                return (colorDecider == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN);
            }
            case BISHOP -> {
                return (colorDecider == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP);
            }
            case KNIGHT -> {
                return (colorDecider == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT);
            }
            case ROOK -> {
                return (colorDecider == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK);
            }
            case PAWN -> {
                return (colorDecider == ChessGame.TeamColor.WHITE ?
                        EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN);
            }
        }
        return "";
    }



    public String exitGame() throws DataException {



        if (!observer){
            server.updateGame(new UpdateGameRequest(this.gameID, this.teamColor, this.token, null, "false", true));
        }
        this.observer = false;
        this.inGame = false;
        ws = new WebSocketFacade(serverUrl,notificationHandler);
        ws.exit(this.token, this.gameID);
        return (this.user + ", you successfully left the game.");

    }

    public String resign() throws DataException {

        chessGame.setGameOver(true);
        this.gameOver = true;
        server.updateGame(new UpdateGameRequest(this.gameID, null, this.token, null, "true", true));
        ws = new WebSocketFacade(serverUrl,notificationHandler);
        ws.resignGame(this.token, this.gameID);
        return "You resigned. Use 'exit' to leave the game";

    }

    public String highlight(String... params) throws DataException {
        if (params.length > 1 || !validPositions().contains(params[0]) ){
            return "Highlight Error! Use: legalmoves <chessposition>";
        }

        ChessPosition piece = transformationPosition(LETTER_TO_NUM, params[0]);
        Collection<ChessMove> allowedMoves = chessGame.validMoves(piece);
        Collection<ChessPosition> allowedMove = new ArrayList<>();

        for (ChessMove move : allowedMoves){
            allowedMove.add(move.getEndPosition());
        }
        return drawBoard(teamColor, observer, allowedMove, this.chessGame);
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public boolean getInGame(){
        return inGame;
    }

    public ChessGame getChessGame(){
        return this.chessGame;
    }

    public boolean getObserver(){
        return observer;
    }


    public String help(){
        return """
                - help -> Displays text informing the user what actions they can take.
                - quit -> Exits the program.
                - board -> Show current board.
                - leave -> Leave game.
                - resign -> resign game.
                - legalmoves -> Highlights allowed moves for a piece.
                - makemove -> <startpos> <endpos>
                """;
    }



}
