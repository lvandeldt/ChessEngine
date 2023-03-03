package org.vandeldt.Chess;

import javax.swing.*;
import java.awt.*;
import java.util.*;

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

    public static Hashtable<Type, Character> TYPE_CHARACTER_HASHTABLE = new Hashtable<>() {{
        put(Piece.Type.ROOK, 'r');
        put(Piece.Type.KNIGHT, 'n');
        put(Piece.Type.BISHOP, 'b');
        put(Piece.Type.QUEEN, 'q');
        put(Piece.Type.KING, 'k');
        put(Piece.Type.PAWN, 'p');
    }};

    private final Team team;
    private Type type;
    private Point position;
    private boolean can_do_special = true;
    private final ChessBoard parent_board;
    private ImageIcon sprite;

    private final HashSet<Movement> allowed_movement = new HashSet<>();

    public Piece(ChessBoard parent_board, Type type, Team team, Point position) {
        this.parent_board = parent_board;
        this.type = type;
        this.team = team;
        this.position = position;
        this.sprite = new ImageIcon(Objects.requireNonNull(getClass().getResource("sprites/" + this.type.toString() + "_" + this.team.toString() + ".png")));
    }

    public Piece(ChessBoard parent_board, Type type, Team team, Point position, boolean can_do_special) {
        this(parent_board, type, team, position);
        this.can_do_special = can_do_special;
    }

    public HashSet<Point> getValidMoves() {

        HashSet<Point> valid_moves = new HashSet<>();

        for (Movement movement : allowed_movement) {
            valid_moves.addAll( movement.generateMoves( this.parent_board.getBoard(), this.position, this.team ) );
        }

        if (this.type == Type.PAWN) {
            valid_moves.addAll( getPawnSpecialMoves() );
        } else if (this.type == Type.KING) {
            valid_moves.addAll( getKingSpecialMoves() );
        }

        return valid_moves;

    }

    private HashSet<Point> getPawnSpecialMoves() {
        int direction = (this.team == Piece.Team.WHITE ? -1 : 1);
        Piece[][] board = this.parent_board.getBoard();
        Point enpassant_square = this.parent_board.getEnpassantSquare();

        HashSet<Point> special_moves = new HashSet<>();

        // Opening double move
        if (this.can_do_special && board[this.position.x + direction][this.position.y] == null
                && board[this.position.x + direction * 2][this.position.y] == null) {

            special_moves.add(new Point(this.position.x + direction * 2, this.position.y));

        }

        // En Passant Capture
        if (enpassant_square != null) {
            if ( enpassant_square.x - this.position.x == direction && Math.abs( enpassant_square.y - this.position.y ) == 1 ) {

                special_moves.add( new Point(enpassant_square.x, enpassant_square.y) );

            }
        }

        return special_moves;
    }

    private HashSet<Point> getKingSpecialMoves() {

        HashSet<Point> special_moves = new HashSet<>();

        if (!this.can_do_special || this.type != Type.KING) {
            return special_moves;
        }

        Piece[][] board = this.parent_board.getBoard();

        // Kingside Castle
        Piece kingside_corner_piece = board[this.position.x][board.length - 1];

        if ( kingside_corner_piece != null ) {

            // If the piece in the corner has can_do_special set true it should be a same colour rook anyway.

            if ( kingside_corner_piece.can_do_special && board[this.position.x][this.position.y + 1] == null &&
                board[this.position.x][this.position.y + 2] == null) {

                special_moves.add(new Point(this.position.x, this.position.y + 2));

            }

        }

        // Queenside Castle
        Piece queenside_corner_piece = board[this.position.x][0];

        if ( queenside_corner_piece != null ) {

            // If the piece in the corner has can_do_special set true it should be a same colour rook anyway.

            if ( queenside_corner_piece.can_do_special && board[this.position.x][this.position.y - 1] == null &&
                    board[this.position.x][this.position.y - 2] == null && board[this.position.x][this.position.y - 3] == null) {

                special_moves.add(new Point(this.position.x, this.position.y - 2));

            }

        }



        return special_moves;
    }

    public void promoteTo(Type promotion) {
        if (this.type != Type.PAWN) {
            return;
        }

        if (promotion == Type.KNIGHT) {
            this.type = Type.KNIGHT;
        } else if (promotion == Type.BISHOP) {
            this.type = Type.BISHOP;
        } else if (promotion == Type.ROOK) {
            this.type = Type.ROOK;
        } else {
            this.type = Type.QUEEN;
        }

        this.allowed_movement.clear();
        this.addMovement( ChessDefaults.CHARACTER_MOVEMENT_HASHTABLE.get( TYPE_CHARACTER_HASHTABLE.get(this.type) ) );
        this.sprite = new ImageIcon(Objects.requireNonNull(getClass().getResource("sprites/" + this.type.toString() + "_" + this.team.toString() + ".png")));

    }

    public void addMovement(Movement movement) {
        this.allowed_movement.add(movement);
    }

    public void addMovement(Movement[] movements) {
        this.allowed_movement.addAll(Arrays.asList(movements));
    }

    public void setPosition(Point position) {
        this.position = position;
        this.can_do_special = false;
    }

    public Team getTeam() {
        return team;
    }

    public Type getType() {
        return type;
    }

    public ImageIcon getSprite() {

        return this.sprite;

    }

}
