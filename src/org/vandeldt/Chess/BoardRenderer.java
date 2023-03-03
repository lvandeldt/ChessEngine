package org.vandeldt.Chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BoardRenderer extends JPanel {

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

    private static final int SQUARE_SIZE = 70;
    private final Color[] BOARD_COLOURS = {new Color(118,150,86), new Color(238, 238, 210)};

    private final ChessBoard render_subject;
    private Point selected_square = null;

    private ChessBoardSquare[][] grid = new ChessBoardSquare[ChessDefaults.BOARD_SIZE][ChessDefaults.BOARD_SIZE];


    public BoardRenderer() {
        this(new ChessBoard());
    }

    public BoardRenderer(ChessBoard chessboard) {
        super(new GridLayout(ChessDefaults.BOARD_SIZE, ChessDefaults.BOARD_SIZE));
        this.render_subject = chessboard;
        this.render();
    }

    public void render() {

        for (int i = 0; i < ChessDefaults.BOARD_SIZE; i++) {

            for (int j = 0; j < ChessDefaults.BOARD_SIZE; j++) {

                ChessBoardSquare square = new ChessBoardSquare(SQUARE_SIZE, (i + j) % 2 == 0 ? BOARD_COLOURS[0] : BOARD_COLOURS[1], new Point(i, j));

                square.addMouseListener(click_listener);
                square.setDisplay( this.render_subject.getPieceAt(i, j) != null ? this.render_subject.getPieceAt(i, j).getSprite() : null );

                this.add(square);

                grid[i][j] = square;
            }

        }

        this.setVisible(true);

        System.out.println("Board Rendered");

    }

    public void paintValidMoves() {

        Piece selected_piece = this.render_subject.getBoard()[selected_square.x][selected_square.y];

        for ( Point position : selected_piece.getValidMoves() ) {

            grid[position.x][position.y].setBackground(Color.RED);
            grid[position.x][position.y].setValidMove(true);

        }

    }

    public void selectSquare(ChessBoardSquare square) {

        Point position = square.getPosition();

        if (selected_square == null && this.render_subject.getPieceAt(position) != null) {
            if (this.render_subject.getPieceAt(position).getTeam() != this.render_subject.getColourToMove()) {
                return;
            }

            selected_square = position;
            square.setBackground( Color.YELLOW );

            paintValidMoves();

            System.out.println("Selected (" + position.x  + ", " + position.y + ").");
        } else if (selected_square == position) {
            selected_square = null;
            System.out.println("Unselected (" + position.x  + ", " + position.y + ").");
            square.update();
            this.update();
        } else if (selected_square != null && square.isValidMove()) {

            this.render_subject.makeMove( selected_square, position );
            selected_square = null;
            System.out.println("Move made.");
            this.update();

        }

    }

    public void update() {

        for (int rank = 0; rank < grid.length; rank++) {
            for (int file = 0; file < grid[0].length; file++) {
                grid[rank][file].setDisplay( this.render_subject.getPieceAt(rank, file) != null ? this.render_subject.getPieceAt(rank, file).getSprite() : null );
                grid[rank][file].update();
                grid[rank][file].setValidMove(false);
            }
        }

    }


}
