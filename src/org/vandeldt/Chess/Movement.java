package org.vandeldt.Chess;

import java.awt.*;
import java.util.HashSet;

public class Movement {

    enum CaptureBehaviour {

        CAN_CAPTURE, CAPTURE_ONLY, NO_CAPTURE

    }

    private final HashSet<Point> directions = new HashSet<>();
    private final boolean repeats;
    private final CaptureBehaviour capture_behaviour;

    public Movement(int[][] directions, boolean repeats) {

        this(directions, repeats, CaptureBehaviour.CAN_CAPTURE);

    }

    public Movement(int[][] directions, boolean repeats, CaptureBehaviour capture_behaviour) {

        for ( int[] direction : directions ) {

            this.directions.add( new Point( direction[0], direction[1] ));

        }

        this.repeats = repeats;
        this.capture_behaviour = capture_behaviour;
    }


    public HashSet<Point> generateMoves(ChessBoardSquare[][] target_board, Point current_position, Piece.Team current_team) {

        HashSet<Point> valid_moves = new HashSet<>();

        DIRECTION: for (Point direction : directions) {

            Point working_position = new Point(current_position);

            do {
                working_position.translate(direction.x, direction.y);

                boolean position_in_bounds = working_position.x >= 0 && working_position.x < target_board.length &&
                        working_position.y >= 0 && working_position.y < target_board.length;

                if (!position_in_bounds) {
                    continue DIRECTION;
                }

                if (target_board[working_position.x][working_position.y].isOccupied()) {
                    if (target_board[working_position.x][working_position.y].getOccupant().getTeam() != current_team && this.capture_behaviour != CaptureBehaviour.NO_CAPTURE) {
                        valid_moves.add(new Point(working_position));
                    }

                    continue DIRECTION;

                } else if (this.capture_behaviour != CaptureBehaviour.CAPTURE_ONLY) {
                    valid_moves.add(new Point(working_position));
                }
            } while (repeats);

        }

        return valid_moves;

    }

}
