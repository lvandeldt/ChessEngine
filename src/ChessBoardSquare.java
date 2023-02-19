import javax.swing.*;
import java.awt.*;

public class ChessBoardSquare extends JPanel {

    private final Color DEFAULT_COLOUR;
    private final JLabel display = new JLabel();
    private Piece occupant;
    private Point position;

    public ChessBoardSquare(int size, Color colour, Point position) {
        this.setPreferredSize(new Dimension(size, size));
        this.DEFAULT_COLOUR = colour;
        this.setBackground(DEFAULT_COLOUR);

        this.position = position;

        this.add(display);
    }

    public Point getPosition() {
        return this.position;
    }

    public void setOccupant(Piece piece) {
        this.occupant = piece;
        this.update();
    }

    public void update() {
        this.display.setIcon( this.occupant != null ? this.occupant.getSprite() : null );
    }

    public Piece getOccupant() {
        return occupant;
    }
}
