package org.vandeldt.Chess;

import java.util.Hashtable;

public class ChessDefaults {
    static final int BOARD_SIZE = 8;
    static final Movement PAWN_BLACK = new Movement( new int[][] { {1, 0} }, false, Movement.CaptureBehaviour.NO_CAPTURE);
    static final Movement PAWN_WHITE = new Movement( new int[][] { {-1, 0} }, false,  Movement.CaptureBehaviour.NO_CAPTURE);
    static final Movement PAWN_CAPTURE_BLACK = new Movement( new int[][] { {1, -1}, {1, 1} }, false, Movement.CaptureBehaviour.CAPTURE_ONLY);
    static final Movement PAWN_CAPTURE_WHITE = new Movement( new int[][] { {-1, -1}, {-1, 1} }, false,  Movement.CaptureBehaviour.CAPTURE_ONLY);
    static final Movement DIAGONAL = new Movement( new int[][] { { 1, 1 }, {-1, -1}, {1, -1}, {-1, 1} }, false );
    static final Movement ORTHOGONAL = new Movement( new int[][] { { 1, 0 }, {-1, 0}, {0, -1}, {0, 1} }, false );
    static final Movement DIAGONAL_REPEATED = new Movement( new int[][] { { 1, 1 }, {-1, -1}, {1, -1}, {-1, 1} }, true );
    static final Movement ORTHOGONAL_REPEATED = new Movement( new int[][] { { 1, 0 }, {-1, 0}, {0, -1}, {0, 1} }, true );
    static final Movement KNIGHT = new Movement( new int[][] { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 },
            { -1, 2 }, { -1, -2 }}, false );
    static final Hashtable<Character, Piece.Type> CHARACTER_TYPE_HASHTABLE = new Hashtable<>() {{

                put('r', Piece.Type.ROOK);
                put('n', Piece.Type.KNIGHT);
                put('b', Piece.Type.BISHOP);
                put('q', Piece.Type.QUEEN);
                put('k', Piece.Type.KING);
                put('p', Piece.Type.PAWN);

            }};
    static Hashtable<Character, Movement[]> CHARACTER_MOVEMENT_HASHTABLE = new Hashtable<>() {{
        put('r', new Movement[] {ORTHOGONAL_REPEATED});
        put('n', new Movement[] {KNIGHT});
        put('b', new Movement[] {DIAGONAL_REPEATED});
        put('q', new Movement[] {DIAGONAL_REPEATED, ORTHOGONAL_REPEATED});
        put('k', new Movement[] {DIAGONAL, ORTHOGONAL});
    }};

}
