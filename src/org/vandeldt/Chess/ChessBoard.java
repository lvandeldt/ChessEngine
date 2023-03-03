package org.vandeldt.Chess;

import javax.swing.*;
import java.awt.*;

public class ChessBoard {

    private Piece[][] board = new Piece[ChessDefaults.BOARD_SIZE][ChessDefaults.BOARD_SIZE];

    private Piece.Team colour_to_move = Piece.Team.WHITE;

    private Point enpassant_square = null;
    private int halfmove_clock = 0;
    private int fullmoves = 0;


    public ChessBoard(String fen) {
        super();
        this.loadFromFEN(fen);
    }

    public ChessBoard() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0");
    }


    public void makeMove(Point from, Point to) {

        board[ to.x ][ to.y ] = board[from.x][from.y];
        board[from.x][from.y] = null;

        board[ to.x ][ to.y ].setPosition( to );

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

        // Check for pawn promotion
        if ( board[ to.x ][ to.y ].getType() == Piece.Type.PAWN && (to.x == 0 || to.x == ChessDefaults.BOARD_SIZE - 1) ) {
            System.out.println("Promote");

            Piece.Type promotion = (Piece.Type) JOptionPane.showInputDialog(null, "Promote Pawn to:", "Pawn Promotion",
                    JOptionPane.PLAIN_MESSAGE, null, new Piece.Type[] {Piece.Type.QUEEN, Piece.Type.ROOK,
                            Piece.Type.KNIGHT, Piece.Type.BISHOP}, Piece.Type.QUEEN);


            board[to.x][to.y].promoteTo( promotion );
        }

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

        this.halfmove_clock++;
        this.fullmoves += (this.colour_to_move == Piece.Team.BLACK) ? 1 : 0;

        this.toggleColourToMove();

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

                board[rank][file] = new Piece(this, type, team, new Point(rank, file), can_do_special);
                board[rank][file].addMovement( movement );

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

}
