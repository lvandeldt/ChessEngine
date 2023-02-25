package org.vandeldt.Chess;

import java.util.Hashtable;

public class DefaultMoves {

    public static final Movement PAWN_BLACK = new Movement( new int[][] { {1, 0} }, false, Movement.CaptureBehaviour.NO_CAPTURE);
    public static final Movement PAWN_WHITE = new Movement( new int[][] { {-1, 0} }, false,  Movement.CaptureBehaviour.NO_CAPTURE);
    public static final Movement PAWN_CAPTURE_BLACK = new Movement( new int[][] { {1, -1}, {1, 1} }, false, Movement.CaptureBehaviour.CAPTURE_ONLY);
    public static final Movement PAWN_CAPTURE_WHITE = new Movement( new int[][] { {-1, -1}, {-1, 1} }, false,  Movement.CaptureBehaviour.CAPTURE_ONLY);
    public static final Movement DIAGONAL = new Movement( new int[][] { { 1, 1 }, {-1, -1}, {1, -1}, {-1, 1} }, false );
    public static final Movement ORTHOGONAL = new Movement( new int[][] { { 1, 0 }, {-1, 0}, {0, -1}, {0, 1} }, false );
    public static final Movement DIAGONAL_REPEATED = new Movement( new int[][] { { 1, 1 }, {-1, -1}, {1, -1}, {-1, 1} }, true );
    public static final Movement ORTHOGONAL_REPEATED = new Movement( new int[][] { { 1, 0 }, {-1, 0}, {0, -1}, {0, 1} }, true );
    public static final Movement KNIGHT = new Movement( new int[][] { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 },
            { -1, 2 }, { -1, -2 }}, false );
    public static Hashtable<Character, Movement[]> CHARACTER_MOVEMENT_HASHTABLE = new Hashtable<>() {{
        put('r', new Movement[] {ORTHOGONAL_REPEATED});
        put('n', new Movement[] {KNIGHT});
        put('b', new Movement[] {DIAGONAL_REPEATED});
        put('q', new Movement[] {DIAGONAL_REPEATED, ORTHOGONAL_REPEATED});
        put('k', new Movement[] {DIAGONAL, ORTHOGONAL});
    }};

}
