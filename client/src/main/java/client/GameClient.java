package client;
import chess.ChessBoard;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;



import exception.DataException;

import server.ServerFacade;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    String user = null;
    String token = null;
    String teamColor = "observer";
    int gameID = 0;
    boolean inGame = true;
    boolean observer = false;
    private WebSocketFacade ws;

    private ChessBoard chessGame;

    public GameClient(String serverUrl, NotificationHandler notificationHandler){
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input, String username, String authToken, String teamColor,
                       boolean observer, boolean inGame, int gameID, ChessBoard chessGame) throws DataException {
        user = username;
        token = authToken;
        this.gameID = gameID;
        this.teamColor = teamColor;
        this.inGame = inGame;
        this.observer = observer;
        this.chessGame = chessGame;
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
            case "help" -> help();
            case "quit" -> "quit";
            case "board" -> drawBoard(teamColor, observer);
            case "leave" -> exitGame();
            case "resign" -> resign();
            case "legalmoves" -> legalMoves();
            case "makemove" -> makeMove(params);
            default -> help();
        };
    }

    public String makeMove(String... params) throws DataException {
        if (params.length == 2) {
            return "GOOD";
        } else {
            return "Invalid format, expected: makemove < startposition> <endposition>";
        }
    }

    public String drawBoard(String teamColor, boolean observer) throws DataException {
        if (observer) {
            teamColor = "white";
        }

        ChessBoard board = chessGame;
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
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                String bg = ((row + col) % 2 == 0)
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
        this.inGame = false;
        this.observer = false;
        ws = new WebSocketFacade(serverUrl,notificationHandler);
        ws.exit(this.token, this.gameID);
        return (this.user + ", you successfully left the game.");

    }

    public String resign() throws DataException {
        ws = new WebSocketFacade(serverUrl,notificationHandler);
        ws.resignGame(this.token, this.gameID);
        return "You resigned. Use 'exit' to leave the game";

    }

    public String legalMoves() throws DataException {
        return "LEGAL MOVES";
    }

    public boolean getInGame(){
        return inGame;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
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
                """;
    }



}
