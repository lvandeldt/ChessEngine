package org.vandeldt.Chess;

import javax.swing.*;
import java.awt.*;

public class ChessBoardSquare extends JPanel {

    private final Color DEFAULT_COLOUR;
    private final JLabel display = new JLabel();
    private Piece occupant;
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

    public Point getPosition() {
        return this.position;
    }

    public void setOccupant(Piece piece) {
        this.occupant = piece;
        this.update();
    }

    public boolean isOccupied() {
        return this.occupant != null;
    }

    public void update() {
        this.display.setIcon( this.occupant != null ? this.occupant.getSprite() : null );
        this.setBackground(DEFAULT_COLOUR);
    }

    public Piece getOccupant() {
        return occupant;
    }
}
