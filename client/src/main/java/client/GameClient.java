package client;
import chess.ChessBoard;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;

import dataaccess.DataAccessException;

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
    boolean inGame = true;
    boolean observer = false;

    public GameClient(String serverUrl, NotificationHandler notificationHandler){
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input, String username, String authToken, String teamColor,
                       boolean observer, boolean inGame) throws DataAccessException {
        user = username;
        token = authToken;
        this.teamColor = teamColor;
        this.inGame = inGame;
        this.observer = observer;
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd){
            case "help" -> help();
            case "quit" -> "quit";
            case "board" -> drawBoard(teamColor, observer);
            case "exit" -> exitGame();

            default -> help();
        };
    }

    public String drawBoard(String teamColor, boolean observer) throws DataAccessException {
        if (observer == true) {
            teamColor = "white";
        }
        ChessBoard board = new ChessBoard();
        List<Integer> rowDecider = new ArrayList<>();
        if (teamColor.equals("white")) {
            rowDecider.add(7);
            rowDecider.add(0);
            rowDecider.add(0);
            rowDecider.add(8);
            rowDecider.add(-1);

        } else {
            rowDecider.add(0);
            rowDecider.add(7);
            rowDecider.add(0);
            rowDecider.add(8);
            rowDecider.add(1);
        }

        board.resetBoard();
        StringBuilder sb = new StringBuilder();
        int direction = rowDecider.get(4);
        sb.append("   a  b  c  d  e  f  g  h\n");

        for (int row = rowDecider.get(0); row != rowDecider.get(1) + direction; row += direction) {
            sb.append(row + 1).append(" ");
            for (int col = rowDecider.get(2); col < rowDecider.get(3); col++) {
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

        sb.append("   a  b  c  d  e  f  g  h\n");

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



    public String exitGame(String... params) throws DataAccessException {
        this.inGame = false;
        this.observer = false;
        return "OUT";

    }

    public boolean getInGame(){
        return inGame;
    }

    public boolean getObserver(){
        return observer;
    }


    public String help(){
        return """
                - help -> Displays text informing the user what actions they can take.
                - quit -> Exits the program.
                - board -> Show current board.
                - exit -> Register to game.
                """;
    }



}
