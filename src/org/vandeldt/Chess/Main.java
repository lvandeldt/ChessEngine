package org.vandeldt.Chess;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        JFrame test = new JFrame("Testing Board Render");

        ChessBoard test2 = new ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0");

        test.add(test2);

        test.pack();
        test.setVisible(true);

    }

}