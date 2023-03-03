package org.vandeldt.Chess;

import javax.swing.*;
import java.awt.*;

public class ChessBoardSquare extends JPanel {

    private final Color DEFAULT_COLOUR;
    private final JLabel display = new JLabel();
    private final Point position;
    private boolean is_valid_move = false;

    public ChessBoardSquare(int size, Color colour, Point position) {
        this.setPreferredSize(new Dimension(size, size));
        this.DEFAULT_COLOUR = colour;
        this.setBackground(DEFAULT_COLOUR);

        this.position = position;

        this.add(display);
    }

    public void setValidMove(boolean is_valid) {
        this.is_valid_move = is_valid;
    }

    public boolean isValidMove() {
        return this.is_valid_move;
    }

    public void setDisplay(ImageIcon icon) {
        this.display.setIcon( icon );
    }

    public Point getPosition() {
        return this.position;
    }

    public void update() {
        this.setBackground(DEFAULT_COLOUR);
    }

}
