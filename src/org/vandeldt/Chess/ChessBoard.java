package org.vandeldt.Chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

public class ChessBoard extends JPanel {

    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 70;

    public ChessBoardSquare[][] getBoard() {
        return board;
    }

    private ChessBoardSquare[][] board = new ChessBoardSquare[BOARD_SIZE][BOARD_SIZE];
    private final Color[] BOARD_COLOURS = {new Color(118,150,86), new Color(238, 238, 210)};
    private Piece.Team colour_to_move = Piece.Team.WHITE;
    private Point selected_square = null;
    private Point enpassant_square = null;
    private int halfmove_clock = 0;
    private int fullmoves = 0;



    private static final Hashtable<Character, Piece.Type> CHARACTER_TYPE_HASHTABLE = new Hashtable<>() {{

        put('r', Piece.Type.ROOK);
        put('n', Piece.Type.KNIGHT);
        put('b', Piece.Type.BISHOP);
        put('q', Piece.Type.QUEEN);
        put('k', Piece.Type.KING);
        put('p', Piece.Type.PAWN);

    }};

    private final MouseListener click_listener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            ChessBoardSquare source = (ChessBoardSquare) e.getSource();
            selectSquare( source );
        }

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

    };

    public ChessBoard(String fen) {
        super();
        this.setLayout( new GridLayout(BOARD_SIZE, BOARD_SIZE));
        this.render();
        this.loadFromFEN(fen);
    }

    public ChessBoard() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0");
    }
    public void render() {

        for (int i = 0; i < BOARD_SIZE; i++) {

            for (int j = 0; j < BOARD_SIZE; j++) {

                ChessBoardSquare square = new ChessBoardSquare(SQUARE_SIZE, (i + j) % 2 == 0 ? BOARD_COLOURS[0] : BOARD_COLOURS[1], new Point(i, j));

                square.addMouseListener(click_listener);

                this.add(square);

                board[i][j] = square;
            }

        }

        this.setVisible(true);

        System.out.println("Board Rendered");

    }

    public void makeMove(Point from, Point to) {

        board[ to.x ][ to.y ].setOccupant(board[from.x][from.y].getOccupant());
        board[from.x][from.y].setOccupant(null);

        board[ to.x ][ to.y ].getOccupant().setPosition( to );

        // Check if we have En Passant captured
        if (to.equals(enpassant_square) && board[ to.x ][ to.y ].getOccupant().getType() == Piece.Type.PAWN) {

            int capture_direction = board[ to.x ][ to.y ].getOccupant().getTeam() == Piece.Team.WHITE ? 1 : -1;

            board[to.x + capture_direction][to.y].setOccupant(null);

        }

        // Check if we have can en passant next turn.
        if ( from.distance(to) == 2 &&  board[ to.x ][ to.y ].getOccupant().getType() == Piece.Type.PAWN) {
            this.enpassant_square = new Point( (from.x + to.x) / 2, from.y );
        } else {
            this.enpassant_square = null;
        }

        // Check for pawn promotion
        if ( board[ to.x ][ to.y ].getOccupant().getType() == Piece.Type.PAWN && (to.x == 0 || to.x == BOARD_SIZE - 1) ) {
            System.out.println("Promote");

            Piece.Type promotion = (Piece.Type) JOptionPane.showInputDialog(null, "Promote Pawn to:", "Pawn Promotion",
                    JOptionPane.PLAIN_MESSAGE, null, new Piece.Type[] {Piece.Type.QUEEN, Piece.Type.ROOK,
                            Piece.Type.KNIGHT, Piece.Type.BISHOP}, Piece.Type.QUEEN);


            board[to.x][to.y].getOccupant().promoteTo( promotion );
        }

        // Check caste
        if ( from.distance(to) == 2 &&  board[ to.x ][ to.y ].getOccupant().getType() == Piece.Type.KING) {

            if ( to.y == BOARD_SIZE - 2 ) {
                board[ to.x ][ to.y - 1].setOccupant(board[to.x][BOARD_SIZE - 1].getOccupant());
                board[ to.x ][BOARD_SIZE - 1].setOccupant(null);

                board[ to.x ][ to.y - 1 ].getOccupant().setPosition( new Point(to.x, to.y - 1) );
            } else if (to.y == 2) {
                board[ to.x ][ to.y + 1].setOccupant(board[to.x][0].getOccupant());
                board[ to.x ][0].setOccupant(null);

                board[ to.x ][ to.y + 1 ].getOccupant().setPosition( new Point(to.x, to.y + 1) );
            }

        }

        this.halfmove_clock++;
        this.fullmoves += (this.colour_to_move == Piece.Team.BLACK) ? 1 : 0;

        this.toggleColourToMove();
        this.update();

    }

    public void toggleColourToMove() {
        this.colour_to_move = this.colour_to_move == Piece.Team.WHITE ? Piece.Team.BLACK : Piece.Team.WHITE;
    }

    public void paintValidMoves() {

        Piece selected_piece = board[selected_square.x][selected_square.y].getOccupant();

        for ( Point position : selected_piece.getValidMoves() ) {

            board[position.x][position.y].setBackground(Color.RED);
            board[position.x][position.y].setValidMove(true);

        }

    }

    public void selectSquare(ChessBoardSquare square) {

        Point position = square.getPosition();

        if (selected_square == null && square.getOccupant() != null) {
            if (square.getOccupant().getTeam() != this.colour_to_move) {
                return;
            }

            selected_square = square.getPosition();
            square.setBackground( Color.YELLOW );

            paintValidMoves();

            System.out.println("Selected (" + position.x  + ", " + position.y + ").");
        } else if (selected_square == position) {
            selected_square = null;
            System.out.println("Unselected (" + position.x  + ", " + position.y + ").");
            square.update();
            this.update();
        } else if (selected_square != null && square.isValidMove()) {

            makeMove( selected_square, position );
            selected_square = null;
            System.out.println("Move made.");

        }

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

                Piece.Type type = CHARACTER_TYPE_HASHTABLE.get(Character.toLowerCase(current_char));
                Piece.Team team = Character.isLowerCase(current_char) ? Piece.Team.BLACK : Piece.Team.WHITE;

                Movement[] movement = DefaultMoves.CHARACTER_MOVEMENT_HASHTABLE.getOrDefault( Character.toLowerCase(current_char),
                        Character.isLowerCase(current_char) ? new Movement[] {DefaultMoves.PAWN_BLACK, DefaultMoves.PAWN_CAPTURE_BLACK} :
                                new Movement[] {DefaultMoves.PAWN_WHITE, DefaultMoves.PAWN_CAPTURE_WHITE} );

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
                                ((file == 0 && queenside_castle) || (file == BOARD_SIZE-1 && kingside_castle)));

                board[rank][file].setOccupant(new Piece(this, type, team, new Point(rank, file), can_do_special));
                board[rank][file].getOccupant().addMovement( movement );

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

    public void update() {

        for (ChessBoardSquare[] row : board) {
            for (ChessBoardSquare square : row) {
                square.update();
                square.setValidMove(false);
            }
        }

    }

    public Point getEnpassantSquare() {
        return enpassant_square;
    }

}
