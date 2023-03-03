package org.vandeldt.Chess;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public class ChessBoard {

    private Piece[][] board = new Piece[ChessDefaults.BOARD_SIZE][ChessDefaults.BOARD_SIZE];
    private Piece.Team colour_to_move = Piece.Team.WHITE;
    private Point enpassant_square = null;
    private int halfmove_clock = 0;
    private int fullmoves = 0;

    private final Hashtable< Piece.Team, Hashtable< Piece.Type, HashSet<Point>>> piece_positions = new Hashtable<>()  {{
        put(Piece.Team.BLACK, new Hashtable<>() {{
            for (Piece.Type type : Piece.Type.values()) {
                put(type,new HashSet<>());
            }
        }});

        put(Piece.Team.WHITE, new Hashtable<>() {{
            for (Piece.Type type : Piece.Type.values()) {
                put(type,new HashSet<>());
            }
        }});
    }};


    public ChessBoard(String fen) {
        super();
        this.loadFromFEN(fen);
    }

    public ChessBoard() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0");
    }


    public void makeMove(Point from, Point to) {
        makeMove(from, to, false);
    }

    public void makeMove(Point from, Point to, boolean test_move) {

        // TODO: Make this method nicer.
        // TODO: Complete piece_positions logic in here. i.e captures and castles.

        board[ to.x ][ to.y ] = board[from.x][from.y];
        board[from.x][from.y] = null;

        checkEnPassant(from, to);
        checkCastle(from, to);


        if (!test_move) {
            checkPromotion(to);

            updatePositionOf( getPieceAt(to).getTeam(), getPieceAt(to).getType(), from, to );

            board[to.x][to.y].setPosition(to);

            this.halfmove_clock++;
            this.fullmoves += (this.colour_to_move == Piece.Team.BLACK) ? 1 : 0;

            this.toggleColourToMove();

            if (this.isInCheck(this.colour_to_move)) {
                System.out.println("Check!");
            }
        }

    }

    private void checkCastle(Point from, Point to) {
        // Check caste
        if ( from.distance(to) == 2 &&  board[ to.x ][ to.y ].getType() == Piece.Type.KING) {

            if ( to.y == ChessDefaults.BOARD_SIZE - 2 ) {
                board[ to.x ][ to.y - 1] = board[to.x][ChessDefaults.BOARD_SIZE - 1];
                board[ to.x ][ChessDefaults.BOARD_SIZE - 1] = null;

                board[ to.x ][ to.y - 1 ].setPosition( new Point(to.x, to.y - 1) );
            } else if (to.y == 2) {
                board[ to.x ][ to.y + 1] = board[to.x][0];

                board[ to.x ][ to.y + 1 ].setPosition( new Point(to.x, to.y + 1) );
            }

        }
    }

    private void checkPromotion(Point to) {
        // Check for pawn promotion
        if ( board[ to.x ][ to.y ].getType() == Piece.Type.PAWN && (to.x == 0 || to.x == ChessDefaults.BOARD_SIZE - 1) ) {
            System.out.println("Promote");

            Piece.Type promotion = (Piece.Type) JOptionPane.showInputDialog(null, "Promote Pawn to:", "Pawn Promotion",
                    JOptionPane.PLAIN_MESSAGE, null, new Piece.Type[] {Piece.Type.QUEEN, Piece.Type.ROOK,
                            Piece.Type.KNIGHT, Piece.Type.BISHOP}, Piece.Type.QUEEN);


            board[to.x][to.y].promoteTo( promotion );
        }
    }

    private void checkEnPassant(Point from, Point to) {
        // Check if we have En Passant captured
        if (to.equals(enpassant_square) && board[ to.x ][ to.y ].getType() == Piece.Type.PAWN) {

            int capture_direction = board[ to.x ][ to.y ].getTeam() == Piece.Team.WHITE ? 1 : -1;

            board[to.x + capture_direction][to.y] = null;

        }

        // Check if we have can en passant next turn.
        if ( from.distance(to) == 2 &&  board[ to.x ][ to.y ].getType() == Piece.Type.PAWN) {
            this.enpassant_square = new Point( (from.x + to.x) / 2, from.y );
        } else {
            this.enpassant_square = null;
        }
    }

    public boolean positionIsThreatened(Point position, Piece.Team for_team) {

        for ( Movement movement: ChessDefaults.THREATENING_MOVES ) {

            for (Point possible_threat: movement.generateMoves( this.board, position, for_team, true )) {

                Movement movement_to_check;

                if (!movement.equals(ChessDefaults.PAWN_CAPTURE_BLACK) && !movement.equals(ChessDefaults.PAWN_CAPTURE_WHITE)) {
                    movement_to_check = movement;
                } else {
                    movement_to_check = movement == ChessDefaults.PAWN_CAPTURE_BLACK ? ChessDefaults.PAWN_CAPTURE_WHITE: ChessDefaults.PAWN_CAPTURE_BLACK;
                }

                if (getPieceAt(possible_threat).hasMovement(movement_to_check)) {
                    return true;
                }

            }

        }

        return false;

    }

    public boolean positionIsThreatened(int rank, int file, Piece.Team for_team) {

        return positionIsThreatened( new Point(rank, file), for_team );

    }

    public boolean isInCheck(Piece.Team team) {

        Point king_pos = (Point) getPositionsOf(team, Piece.Type.KING).toArray()[0];

        return positionIsThreatened(king_pos, team);

    }

    public HashSet<Point> movesForPieceAt(Point position) {
        Piece piece = getPieceAt(position);

        if (piece == null) {
            return null;
        }

        HashSet<Point> valid_moves = piece.getValidMoves();

        for (Iterator<Point> iterator = valid_moves.iterator(); iterator.hasNext(); ) {

            Point move = iterator.next();

            Piece[][] board_before = copyBoardState();

            makeMove(position, move, true);

            if (piece.getType() != Piece.Type.KING && this.isInCheck(piece.getTeam())) {
                iterator.remove();
            } else if (piece.getType() == Piece.Type.KING && positionIsThreatened(move, piece.getTeam())) {
                iterator.remove();
            }

            this.board = board_before;

        }

        return valid_moves;
    }

    public Piece[][] copyBoardState() {

        Piece[][] copy = new Piece[board.length][board[0].length];

        for (int rank = 0; rank < board.length; rank++) {
            System.arraycopy(board[rank], 0, copy[rank], 0, board[0].length);
        }

        return copy;

    }

    public void toggleColourToMove() {
        this.colour_to_move = this.colour_to_move == Piece.Team.WHITE ? Piece.Team.BLACK : Piece.Team.WHITE;
    }

    public void loadFromFEN(String fen_string) {

        if (fen_string == null) {
            return;
        }

        String[] fen_args = fen_string.split(" ");

        int rank = 0;
        int file = 0;

        for (int i= 0; i < fen_args[0].length(); i++) {

            char current_char = fen_args[0].charAt(i);

            if ( Character.isDigit(current_char) ) {
                file = file + Character.getNumericValue(current_char);
            } else if (current_char == '/') {
                rank++;
                file = 0;
            } else {

                Piece.Type type = ChessDefaults.CHARACTER_TYPE_HASHTABLE.get(Character.toLowerCase(current_char));
                Piece.Team team = Character.isLowerCase(current_char) ? Piece.Team.BLACK : Piece.Team.WHITE;

                Movement[] movement = ChessDefaults.CHARACTER_MOVEMENT_HASHTABLE.getOrDefault( Character.toLowerCase(current_char),
                        Character.isLowerCase(current_char) ? new Movement[] {ChessDefaults.PAWN_BLACK, ChessDefaults.PAWN_CAPTURE_BLACK} :
                                new Movement[] {ChessDefaults.PAWN_WHITE, ChessDefaults.PAWN_CAPTURE_WHITE} );

                boolean kingside_castle = false;
                boolean queenside_castle = false;
                if (type == Piece.Type.KING || type == Piece.Type.ROOK) {
                    if (team == Piece.Team.WHITE) {
                        kingside_castle = fen_args[2].contains("K");
                        queenside_castle = fen_args[2].contains("Q");
                    } else {
                        kingside_castle = fen_args[2].contains("k");
                        queenside_castle = fen_args[2].contains("q");
                    }
                }

                boolean can_do_special = (type == Piece.Type.PAWN && rank == (team == Piece.Team.BLACK ? 1 : 6)) ||
                        (type == Piece.Type.KING && (kingside_castle || queenside_castle)) ||
                        (type == Piece.Type.ROOK && rank == (team == Piece.Team.BLACK ? 0 : 7) &&
                                ((file == 0 && queenside_castle) || (file == ChessDefaults.BOARD_SIZE-1 && kingside_castle)));

                Point start_position = new Point(rank, file);

                board[rank][file] = new Piece(this, type, team, start_position, can_do_special);
                board[rank][file].addMovement( movement );

                this.piece_positions.get(team).get(type).add(start_position);

                file++;

            }

        }

        this.colour_to_move = fen_args[1].equals("w") ? Piece.Team.WHITE: Piece.Team.BLACK;

        if (!fen_args[3].equals("-")) {

            int enpassant_rank = 8 - Character.getNumericValue(fen_args[3].charAt(1));

            int enpassant_file = (int) fen_args[3].charAt(0) - 'a';

            this.enpassant_square = new Point(enpassant_rank, enpassant_file);

            System.out.println(enpassant_square);
        }

        this.halfmove_clock = Integer.parseInt(fen_args[4]);
        this.fullmoves = Integer.parseInt(fen_args[5]);

    }

    public Point getEnpassantSquare() {
        return enpassant_square;
    }

    public Piece.Team getColourToMove() {
        return this.colour_to_move;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public Piece getPieceAt(int rank, int file) {
        return this.board[rank][file];
    }

    public Piece getPieceAt(Point position) {
        return getPieceAt(position.x, position.y);
    }

    public HashSet<Point> getPositionsOf(Piece.Team team, Piece.Type type) {
        return this.piece_positions.get(team).get(type);
    }

    private void updatePositionOf(Piece.Team team, Piece.Type type, Point from, Point to) {
        HashSet<Point> positions = this.piece_positions.get(team).get(type);

        if (!positions.contains(from)) {
            System.out.println("Attempted update of invalid position.");
            return;
        }

        positions.remove(from);
        positions.add(to);
    }

}
