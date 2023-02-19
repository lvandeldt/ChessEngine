import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Piece {

    enum Team {
        WHITE, BLACK
    }

    enum Type {
        PAWN(1), BISHOP(3), KNIGHT(3), ROOK(5), QUEEN(9), KING(Integer.MAX_VALUE);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int get_value() {
            return this.value;
        }
    }

    public Team team;
    public Type type;
    public Point position = new Point();

    public Piece(Type type, Team team) {
        this.type = type;
        this.team = team;
    }

    public ImageIcon getSprite() {

        return new ImageIcon(Objects.requireNonNull(getClass().getResource("sprites/" + this.type.toString() + "_" + this.team.toString() + ".png")));

    }

}
