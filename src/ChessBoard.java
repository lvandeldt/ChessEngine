import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

public class ChessBoard extends JPanel {

    private static final Hashtable<Character, Piece.Type> character_pieces = new Hashtable<>() {{

        put('r', Piece.Type.ROOK);
        put('n', Piece.Type.KNIGHT);
        put('b', Piece.Type.BISHOP);
        put('q', Piece.Type.QUEEN);
        put('k', Piece.Type.KING);
        put('p', Piece.Type.PAWN);

    }};

    private static final int SQUARE_SIZE = 70;
    private final int BOARD_SIZE = 8;
    private final Color[] BOARD_COLOURS = {new Color(118,150,86), new Color(238, 238, 210)};

    public ChessBoardSquare[][] board = new ChessBoardSquare[BOARD_SIZE][BOARD_SIZE];

    private Piece.Team colour_to_move = Piece.Team.WHITE;

    private Point selected_square = null;

    public ChessBoard(String fen) {
        super();
        this.setLayout( new GridLayout(BOARD_SIZE, BOARD_SIZE));
        this.render();
        this.load_from_FEN(fen);
    }

    public ChessBoard() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    }
    public void render() {

        for (int i = 0; i < BOARD_SIZE; i++) {

            for (int j = 0; j < BOARD_SIZE; j++) {

                ChessBoardSquare square = new ChessBoardSquare(SQUARE_SIZE, (i + j) % 2 == 0 ? BOARD_COLOURS[0] : BOARD_COLOURS[1], new Point(i, j));

                square.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {}

                    @Override
                    public void mousePressed(MouseEvent e) {
                        ChessBoardSquare source = (ChessBoardSquare) e.getSource();
                        selectSquare( source.getPosition() );
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {}

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}

                });

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

    }

    public void selectSquare(Point point) {

        if (selected_square == null && board[point.x][point.y].getOccupant() != null) {
            selected_square = point;
            System.out.println("Selected (" + point.x  + ", " + point.y + ").");
        } else if (selected_square == point) {
            selected_square = null;
            System.out.println("Unselected (" + point.x  + ", " + point.y + ").");
        } else if (selected_square != null) {
            makeMove( selected_square, point );
            selected_square = null;
            System.out.println("Move made.");
        }

    }

    public void load_from_FEN(String fen_string) {

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

                board[rank][file].setOccupant(new Piece( character_pieces.get(Character.toLowerCase(current_char)),
                        Character.isLowerCase(current_char) ? Piece.Team.BLACK : Piece.Team.WHITE));

                file++;

            }

        }

        this.colour_to_move = fen_args[1].equals("w") ? Piece.Team.WHITE: Piece.Team.BLACK;

    }

}
